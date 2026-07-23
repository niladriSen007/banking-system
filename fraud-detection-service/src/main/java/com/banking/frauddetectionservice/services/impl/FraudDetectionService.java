package com.banking.frauddetectionservice.services.impl;

import com.banking.frauddetectionservice.client.AccountServiceClient;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FraudDetectionService implements IFraudDetectionService {

    private final CleanTransactionProducer cleanTransactionProducer;
    AccountServiceClient accountServiceClient;
    VerificationRequiredProducer verificationRequiredProducer;

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
        if (senderAccountBalance.compareTo(BigDecimal.ZERO) > 0 && isBalanceCheckFailed(senderAccountNumber, amountToBeTransferred)) {
            return new FraudResponse(true, "Transaction exceeded 90% of account balance");
        }
        return new FraudResponse(false, null);
    }
}
