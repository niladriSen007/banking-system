package com.banking.transactionservice.service.impl;

import com.banking.transactionservice.client.AccountServiceClient;
import com.banking.transactionservice.constants.Topic;
import com.banking.transactionservice.dto.request.TransactionRequest;
import com.banking.transactionservice.dto.response.TransactionResponse;
import com.banking.transactionservice.entity.Transaction;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import com.banking.transactionservice.event.TransactionInitiatedEvent;
import com.banking.transactionservice.exceptions.TransactionNotFoundException;
import com.banking.transactionservice.mapper.Mapper;
import com.banking.transactionservice.producer.TransferInitiatedProducer;
import com.banking.transactionservice.repository.TransactionRepository;
import com.banking.transactionservice.service.ITransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionService implements ITransactionService {

    TransactionRepository transactionRepository;
    AccountServiceClient accountServiceClient;
    TransferInitiatedProducer transferInitiatedProducer;

    /**
     * SAGA Step 1 - >
     * Deducts from sender via feign/webclient
     * Saves transaction as state PROCESSING
     * Publish event to kafka to check Fraud Detection
     *
     * @param transactionRequest
     * @return
     */
    @Override
    public TransactionResponse transferAmount(TransactionRequest transactionRequest) {
        // log add
        accountServiceClient.debitBalance(transactionRequest.getSenderAccountNumber(), transactionRequest.getAmount());

        Transaction build = Transaction.builder()
                .senderAccountNumber(transactionRequest.getSenderAccountNumber())
                .receiverAccountNumber(transactionRequest.getReceiverAccountNumber())
                .amount(transactionRequest.getAmount())
                .transactionStatus(TransactionStatus.PROCESSING)
                .transactionType(TransactionType.TRANSFER)
                .referenceNumber(UUID.randomUUID().toString())
                .build();
        Transaction savedTransaction = transactionRepository.save(build);

        TransactionInitiatedEvent transactionInitiatedEvent = TransactionInitiatedEvent.builder()
                .referenceNumber(savedTransaction.getReferenceNumber())
                .amount(savedTransaction.getAmount())
                .senderAccountNumber(savedTransaction.getSenderAccountNumber())
                .recipientAccountNumber(transactionRequest.getSenderAccountNumber())
                .description(savedTransaction.getDescription())
                .build();

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
    public TransactionResponse verifyOtp(String transactionId, String otp) {
        return null;
    }
}
