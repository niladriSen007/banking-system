package com.banking.transactionservice.consumer;

import com.banking.transactionservice.constants.Topic;
import com.banking.transactionservice.event.NoFraudEvent;
import com.banking.transactionservice.event.VerificationRequiredEvent;
import com.banking.transactionservice.service.ITransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FraudCheckCleanEventConsumer {

    ITransactionService transactionService;

    @KafkaListener(topics = Topic.FRAUD_CHECK_CLEAN_TOPIC, groupId = "fraud-check-clean-group")
    public void handleFraudCheckCleanEvent(@Payload NoFraudEvent payload) {
        String referenceNumber = payload.getReferenceNumber();
        transactionService.processCleanTransaction(referenceNumber);
    }
}
