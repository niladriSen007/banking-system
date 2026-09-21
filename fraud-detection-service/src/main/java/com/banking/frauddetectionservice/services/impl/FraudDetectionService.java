package com.banking.frauddetectionservice.services.impl;

import com.banking.frauddetectionservice.client.AccountServiceClient;
import com.banking.frauddetectionservice.client.TransactionServiceClient;
import com.banking.frauddetectionservice.constants.Topics;
import com.banking.frauddetectionservice.dto.shared.ApiResponse;
import com.banking.frauddetectionservice.dto.shared.FraudResponse;
import com.banking.frauddetectionservice.entity.OutboxEvent;
import com.banking.frauddetectionservice.events.CleanTransactionEvent;
import com.banking.frauddetectionservice.events.TransactionInitiatedEvent;
import com.banking.frauddetectionservice.events.VerificationRequiredEvent;
import com.banking.frauddetectionservice.producer.CleanTransactionProducer;
import com.banking.frauddetectionservice.producer.VerificationRequiredProducer;
import com.banking.frauddetectionservice.repository.OutboxRepository;
import com.banking.frauddetectionservice.services.IFraudDetectionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE)
public class FraudDetectionService implements IFraudDetectionService {

    private final CleanTransactionProducer cleanTransactionProducer;
    private final AccountServiceClient accountServiceClient;
    private final TransactionServiceClient transactionServiceClient;
    private final VerificationRequiredProducer verificationRequiredProducer;
    private final RedisTemplate<String, String> redisTemplate;
    private final OutboxRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    static int MAX_TRANSACTIONS_PER_MINUTE = 6;
    static int SUSPICIOUS_AMOUNT_MULTIPLIER = 3;
    static float MAX_BALANCE_PERCENTAGE = 0.9f;

    @Override
    public void checkTransactionFraud(TransactionInitiatedEvent transactionInitiatedEvent) {
        String transactionReferenceNumber = transactionInitiatedEvent.getReferenceNumber();
        String senderAccountNumber = transactionInitiatedEvent.getSenderAccountNumber();
        BigDecimal amountToBeTransferred = transactionInitiatedEvent.getAmount();

        ApiResponse<BigDecimal> accountBalancerResponse = accountServiceClient.getAccountBalance(senderAccountNumber);
        BigDecimal senderAccountBalance = accountBalancerResponse.getData();

        FraudResponse result = performFraudChecks(senderAccountNumber, senderAccountBalance, amountToBeTransferred);


        if (result.isFraud()) {
            log.info("Suspicious activity detected = {} for user account - {}, Verify OTP before proceeding ", result.getReason(), senderAccountNumber);

            VerificationRequiredEvent verificationRequireEvent = VerificationRequiredEvent.builder()
                    .referenceNumber(transactionReferenceNumber)
                    .senderAccountNumber(senderAccountNumber)
                    .amount(amountToBeTransferred)
                    .reason(result.getReason())
                    .isFraud(true)
                    .build();

//            verificationRequiredProducer.publishVerificationRequiredEvent(
//                    Topics.VERIFICATION_REQUIRED_TOPIC,
//                    transactionReferenceNumber,
//                    verificationRequireEvent
//            );
            publishEvent(Topics.VERIFICATION_REQUIRED_TOPIC, transactionReferenceNumber, verificationRequireEvent);

            System.out.println("Verification required for transaction: " + transactionReferenceNumber+" published to topic: "+Topics.VERIFICATION_REQUIRED_TOPIC);
        } else {
            // No fraud so happy path
            CleanTransactionEvent cleanTransactionEvent = CleanTransactionEvent.builder()
                    .referenceNumber(transactionReferenceNumber)
                    .senderAccountNumber(senderAccountNumber)
                    .amount(amountToBeTransferred)
                    .reason(null)
                    .isFraud(false)
                    .build();

//            cleanTransactionProducer.publishCleanTransactionEvent(
//                    Topics.FRAUD_CHECK_CLEAN_TOPIC,
//                    transactionReferenceNumber,
//                    cleanTransactionEvent
//            );
            publishEvent(Topics.FRAUD_CHECK_CLEAN_TOPIC, transactionReferenceNumber, cleanTransactionEvent);

            System.out.println("No fraud detected for transaction: " + transactionReferenceNumber+" published to topic: "+Topics.FRAUD_CHECK_CLEAN_TOPIC);
        }


    }

    private FraudResponse performFraudChecks(String senderAccountNumber, BigDecimal senderAccountBalance, BigDecimal amountToBeTransferred) {
        if (isVelocityExceeded(senderAccountNumber)) {
            return new FraudResponse(true, "Too many transactions have been verified within a minute");
        }
        if (isAmountSuspicious(senderAccountNumber, amountToBeTransferred)) {
            return new FraudResponse(true, "Unusual transaction amount, exceeds 3X your average transfer");
        }
        if (senderAccountBalance.compareTo(BigDecimal.ZERO) > 0 && isBalanceCheckFailed(senderAccountBalance, amountToBeTransferred)) {
            return new FraudResponse(true, "Transaction exceeded 90% of account balance");
        }
        return new FraudResponse(false, null);
    }

    private boolean isBalanceCheckFailed(BigDecimal senderAccountBalance, BigDecimal amountToBeTransferred) {
        BigDecimal maxAllowed = senderAccountBalance.multiply(
                BigDecimal.valueOf(MAX_BALANCE_PERCENTAGE));

        log.info("Balance check - amount: {} maxAllowed: {} suscpious: {}",
                amountToBeTransferred, maxAllowed, amountToBeTransferred.compareTo(maxAllowed) > 0);

        return amountToBeTransferred.compareTo(maxAllowed) > 0;
    }

    private boolean isAmountSuspicious(String senderAccountNumber, BigDecimal amountToBeTransferred) {
        String averageKey = "fraud:average_amount:" + senderAccountNumber;
        String oldAverageAmount = redisTemplate.opsForValue().get(averageKey);
        if (oldAverageAmount == null) {
            redisTemplate.opsForValue().set(averageKey, amountToBeTransferred.toString());
            return false;
        }
        BigDecimal averageAmount = new BigDecimal(oldAverageAmount);
        BigDecimal thresholdAmount = averageAmount.multiply(BigDecimal.valueOf(SUSPICIOUS_AMOUNT_MULTIPLIER));

        boolean isSuspicious = amountToBeTransferred.compareTo(thresholdAmount) > 0;

        log.info("Amount check - amount: {} threshold: {} suspicious: {}",
                amountToBeTransferred, thresholdAmount, isSuspicious);

        if (!isSuspicious) {
            BigDecimal newAverage = averageAmount.add(amountToBeTransferred)
                    .divide(
                            BigDecimal.valueOf(transactionServiceClient.getTransactionCount(senderAccountNumber).getData() + 1),
                            2,
                            RoundingMode.HALF_UP);
            redisTemplate.opsForValue().set(averageKey, newAverage.toString());
        }
        return isSuspicious;
    }

    private boolean isVelocityExceeded(String senderAccountNumber) {
        String key = "fraud:velocity:" + senderAccountNumber;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }
        log.info("Velocity check for account - {}." +
                " Already done {} transactions from {} transactions", senderAccountNumber, count, MAX_TRANSACTIONS_PER_MINUTE);
        return count > MAX_TRANSACTIONS_PER_MINUTE;
    }



    /**
     * Outbox pattern is used to ensure that the event is published only after the transaction is committed successfully.
     * The event is stored in the outbox table and then published to the Kafka topic.
     * The outbox table is then cleaned up by a scheduled job.
     * This ensures that the event is published only after the transaction is committed successfully.
     * The outbox table is then cleaned up by a scheduled job.
     */
    private void publishEvent(String topic, String referenceNumber, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("fraud-detection-service")
                    .aggregateId(referenceNumber)
                    .type(topic)
                    .payload(payload)
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to serialize TransactionCompletedEvent", e);
            throw new IllegalStateException(
                    "Failed to serialize transaction completed event",
                    e
            );
        }
    }


}
