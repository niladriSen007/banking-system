package com.banking.transactionservice.service.impl;

import com.banking.transactionservice.client.AccountServiceClient;
import com.banking.transactionservice.constants.Topic;
import com.banking.transactionservice.dto.request.TransactionRequest;
import com.banking.transactionservice.dto.response.TransactionResponse;
import com.banking.transactionservice.entity.Transaction;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import com.banking.transactionservice.event.AmountRefundEvent;
import com.banking.transactionservice.event.FraudDetectedEvent;
import com.banking.transactionservice.event.TransactionCompletedEvent;
import com.banking.transactionservice.event.TransactionInitiatedEvent;
import com.banking.transactionservice.exceptions.TransactionNotFoundException;
import com.banking.transactionservice.mapper.Mapper;
import com.banking.transactionservice.producer.AmountRefundProducer;
import com.banking.transactionservice.producer.FraudDetectedProducer;
import com.banking.transactionservice.producer.TransactionCompletedProducer;
import com.banking.transactionservice.producer.TransferInitiatedProducer;
import com.banking.transactionservice.repository.TransactionRepository;
import com.banking.transactionservice.service.ITransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionService implements ITransactionService {

    RedisTemplate<String, String> redisTemplate;
    TransactionRepository transactionRepository;
    AccountServiceClient accountServiceClient;
    TransferInitiatedProducer transferInitiatedProducer;
    AmountRefundProducer amountRefundProducer;
    FraudDetectedProducer fraudDetectedProducer;
    TransactionCompletedProducer transactionCompletedProducer;

    /**
     * SAGA Step 1 - >
     * Deducts from sender via feign/webclient
     * Saves transaction as state PROCESSING
     * Publish event to kafka to check Fraud Detection
     * which will be consumed by FRAUD-DETECTION-SERVICE
     * and do necessary checking
     *
     * @param transactionRequest
     * @return transactionResponse
     */
    @Override
    public TransactionResponse transferAmount(TransactionRequest transactionRequest) {
        log.info("Transfer amount request received in TransactionService from sender {} to receiver {}",
                transactionRequest.getSenderAccountNumber(), transactionRequest.getReceiverAccountNumber());

        // Calling the ACCOUNT-SERVICE to deduct/debit the balance from sender account
        accountServiceClient.debitBalance(transactionRequest.getSenderAccountNumber(), transactionRequest.getAmount());

        Transaction build = Transaction.builder()
                .senderAccountNumber(transactionRequest.getSenderAccountNumber())
                .receiverAccountNumber(transactionRequest.getReceiverAccountNumber())
                .amount(transactionRequest.getAmount())
                .transactionStatus(TransactionStatus.PROCESSING)
                .transactionType(TransactionType.TRANSFER)
                .referenceNumber(UUID.randomUUID().toString())
                .build();

        // saving the transaction history and the transactionStatus will be 'PROCESSING'
        Transaction savedTransaction = transactionRepository.save(build);

        TransactionInitiatedEvent transactionInitiatedEvent = TransactionInitiatedEvent.builder()
                .referenceNumber(savedTransaction.getReferenceNumber())
                .amount(savedTransaction.getAmount())
                .senderAccountNumber(savedTransaction.getSenderAccountNumber())
                .recipientAccountNumber(transactionRequest.getSenderAccountNumber())
                .description(savedTransaction.getDescription())
                .build();

        // publishing the "transaction.initiated" topic so the FRAUD-DETECTION-SERVICE will consume this
        // and do necessary checks
        transferInitiatedProducer.publishTransactionInitiatedEvent(
                Topic.TRANSACTION_INITIATED_TOPIC,
                savedTransaction.getReferenceNumber(),
                transactionInitiatedEvent
        );

        return Mapper.toTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse getTransactionInfo(String transactionReferenceNumber) {
        Transaction transactionDetails = transactionRepository.getTransactionsByReferenceNumber(transactionReferenceNumber).
                orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return Mapper.toTransactionResponse(transactionDetails);
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(Long accountNumber) {
        return transactionRepository
                .findBySenderAccountNumberOrderByCreatedAtDesc(accountNumber)
                .stream()
                .map(Mapper::toTransactionResponse)
                .toList();
    }

    @Override
    public TransactionResponse verifyOtp(String transactionReferenceNumber, String otp) {

        log.info("Verifying OTP for transaction reference number {}", transactionReferenceNumber);
        Transaction transaction = transactionRepository.findByReferenceNumber(transactionReferenceNumber)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        // Match with the otp stored in REDIS in VerificationRequiredEventConsumer class
        String otpKey = "verification:otp" + transactionReferenceNumber;
        String otpVal = redisTemplate.opsForValue().get(otpKey);
        if (otpVal == null) {
            log.warn("OTP expired for transaction: {}", transactionReferenceNumber);
            compensateTransaction(transaction, "OTP expired - transaction cancelled and amount refunded");
            return Mapper.toTransactionResponse(transaction);
        }

        if (!otpVal.equals(otp)) {
            log.warn("Wrong OTP - blocking account and refunding: {}", transactionReferenceNumber);
            redisTemplate.delete(otpKey);
            blockAccountAndCompensate(transaction,
                    "Wrong OTP entered - transaction cancelled, " +
                            "account blocked for security");
            return Mapper.toTransactionResponse(transaction);
        }
        log.info("OTP verified - completing transaction: {}", transactionReferenceNumber);
        redisTemplate.delete(otpKey);
        completeTransaction(transaction);
        return Mapper.toTransactionResponse(transaction);
    }

    private void completeTransaction(Transaction transaction) {
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        TransactionCompletedEvent transactionCompletedEvent = TransactionCompletedEvent
                .builder()
                .senderAccountNumber(transaction.getSenderAccountNumber())
                .receiverAccountNumber(transaction.getReceiverAccountNumber())
                .amount(transaction.getAmount())
                .referenceNumber(transaction.getReferenceNumber())
                .description(transaction.getDescription())
                .build();

        transactionCompletedProducer.publishTransactionCompletedEvent(
                Topic.TRANSACTION_COMPLETED_TOPIC,
                transaction.getReferenceNumber(),
                transactionCompletedEvent
        );

        log.info("SAGA COMPLETE - Transaction {} completed",
                transaction.getId());

    }

    private void blockAccountAndCompensate(Transaction transaction, String reason) {
        // Publish fraud.detected -> Account Service will block account
        FraudDetectedEvent fraudDetectedEvent = FraudDetectedEvent.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .senderAccountNumber(transaction.getSenderAccountNumber())
                .reason(reason)
                .build();

        fraudDetectedProducer.publishFraudDetectedEvent(
                Topic.FRAUD_DETECTED_TOPIC,
                transaction.getReferenceNumber(),
                fraudDetectedEvent
        );

        log.warn("fraud.detected published - account: {} will be blocked, Kindly contact to the bank",
                transaction.getSenderAccountNumber());

        // SAGA COMPENSATION - refund Sender
        compensateTransaction(transaction, reason);
    }

    private void compensateTransaction(Transaction transaction, String reason) {
        log.warn("SAGA COMPENSATION - refunding: {} amount: {}",
                transaction.getSenderAccountNumber(),
                transaction.getAmount());
        // CREDIT MONEY BACK TO SENDER SYNCHRONOUSLY
        accountServiceClient.creditBalance(
                transaction.getSenderAccountNumber(),
                transaction.getAmount());

        transaction.setTransactionStatus(TransactionStatus.FLAGGED);
        transaction.setFailureReason(reason +
                " - SAGA Compensation executed, amount refunded at " + LocalDateTime.now());

        transactionRepository.save(transaction);

        // PUBLISH refund event - Notification service will alert user
        AmountRefundEvent amountRefundEvent = AmountRefundEvent.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .amount(transaction.getAmount())
                .reason(reason)
                .senderAccountNumber(transaction.getSenderAccountNumber())
                .build();

        amountRefundProducer.publishAmountRefundEvent(
                Topic.TRANSACTION_REFUNDED_TOPIC,
                transaction.getReferenceNumber(),
                amountRefundEvent
        );

        log.info("SAGA COMPENSATION COMPLETE - {} refunded to  {}",
                transaction.getAmount(), transaction.getSenderAccountNumber());
    }


    /**
     * Get all the transaction count for a specific account
     * It is required in the Fraud Detection Service to get the total average of the user transactions
     *
     * @param accountNumber
     * @return
     */
    @Override
    public Long getTotalTransactionCount(Long accountNumber) {
        return transactionRepository.getTransactionCount(accountNumber);
    }

    @Override
    public void processCleanTransaction(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new TransactionNotFoundException(referenceNumber));

        if (transaction.getTransactionStatus() != TransactionStatus.PROCESSING) {
            log.warn("Transaction {} not PROCESSING - skipping", referenceNumber);
            return;
        }

        completeTransaction(transaction);
    }
}
