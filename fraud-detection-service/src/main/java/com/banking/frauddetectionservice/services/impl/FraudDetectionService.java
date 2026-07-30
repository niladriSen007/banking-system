package com.banking.frauddetectionservice.services.impl;

import com.banking.frauddetectionservice.client.AccountServiceClient;
import com.banking.frauddetectionservice.client.TransactionServiceClient;
import com.banking.frauddetectionservice.constants.Topics;
import com.banking.frauddetectionservice.dto.shared.ApiResponse;
import com.banking.frauddetectionservice.dto.shared.FraudResponse;
import com.banking.frauddetectionservice.events.CleanTransactionEvent;
import com.banking.frauddetectionservice.events.TransactionInitiatedEvent;
import com.banking.frauddetectionservice.events.VerificationRequiredEvent;
import com.banking.frauddetectionservice.producer.CleanTransactionProducer;
import com.banking.frauddetectionservice.producer.VerificationRequiredProducer;
import com.banking.frauddetectionservice.services.IFraudDetectionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FraudDetectionService implements IFraudDetectionService {

    private final CleanTransactionProducer cleanTransactionProducer;
    AccountServiceClient accountServiceClient;
    TransactionServiceClient transactionServiceClient;
    VerificationRequiredProducer verificationRequiredProducer;
    RedisTemplate<String, String> redisTemplate;
    static int MAX_TRANSACTIONS_PER_MINUTE = 6;
    static int SUSPICIOUS_AMOUNT_MULTIPLIER = 3;
    static float MAX_BALANCE_PERCENTAGE = 0.9f;

    @Override
    public void checkTransactionFraud(TransactionInitiatedEvent transactionInitiatedEvent) {
        String transactionReferenceNumber = transactionInitiatedEvent.getReferenceNumber();
        Long senderAccountNumber = transactionInitiatedEvent.getSenderAccountNumber();
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

            verificationRequiredProducer.publishVerificationRequiredEvent(
                    Topics.VERIFICATION_REQUIRED_TOPIC,
                    transactionReferenceNumber,
                    verificationRequireEvent
            );
        } else {
            // No fraud so happy path
            CleanTransactionEvent cleanTransactionEvent = CleanTransactionEvent.builder()
                    .referenceNumber(transactionReferenceNumber)
                    .senderAccountNumber(senderAccountNumber)
                    .amount(amountToBeTransferred)
                    .reason(null)
                    .isFraud(false)
                    .build();

            cleanTransactionProducer.publishCleanTransactionEvent(
                    Topics.FRAUD_CHECK_CLEAN_TOPIC,
                    transactionReferenceNumber,
                    cleanTransactionEvent
            );
        }


    }

    private FraudResponse performFraudChecks(Long senderAccountNumber, BigDecimal senderAccountBalance, BigDecimal amountToBeTransferred) {
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

    private boolean isAmountSuspicious(Long senderAccountNumber, BigDecimal amountToBeTransferred) {
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

    private boolean isVelocityExceeded(Long senderAccountNumber) {
        String key = "fraud:velocity:" + senderAccountNumber;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }
        log.info("Velocity check for account - {}." +
                " Already done {} transactions from {} transactions", senderAccountNumber, count, MAX_TRANSACTIONS_PER_MINUTE);
        return count > MAX_TRANSACTIONS_PER_MINUTE;
    }


}
