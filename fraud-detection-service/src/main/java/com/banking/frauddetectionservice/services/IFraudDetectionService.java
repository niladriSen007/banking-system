package com.banking.frauddetectionservice.services;

import com.banking.frauddetectionservice.events.TransactionInitiatedEvent;

public interface IFraudDetectionService {
    void checkTransactionFraud(TransactionInitiatedEvent transactionInitiatedEvent);
}
