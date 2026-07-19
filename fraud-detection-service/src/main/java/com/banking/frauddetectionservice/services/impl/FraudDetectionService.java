package com.banking.frauddetectionservice.services.impl;

import com.banking.frauddetectionservice.client.AccountServiceClient;
import com.banking.frauddetectionservice.dto.shared.ApiResponse;
import com.banking.frauddetectionservice.events.TransactionInitiatedEvent;
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

    AccountServiceClient accountServiceClient;

    @Override
    public void checkTransactionFraud(TransactionInitiatedEvent transactionInitiatedEvent) {
        String transactionReferenceNumber = transactionInitiatedEvent.getReferenceNumber();
        Long senderAccountNumber = transactionInitiatedEvent.getSenderAccountNumber();
        BigDecimal amount = transactionInitiatedEvent.getAmount();

        ApiResponse<BigDecimal> accountBalancerResponse = accountServiceClient.getAccountBalance(senderAccountNumber);
        BigDecimal balance = accountBalancerResponse.getData();


    }
}
