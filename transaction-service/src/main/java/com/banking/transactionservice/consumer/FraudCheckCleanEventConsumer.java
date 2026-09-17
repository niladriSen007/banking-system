package com.banking.transactionservice.consumer;

import com.banking.transactionservice.constants.ConsumerGroup;
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

    /**
     * This method listens to the FRAUD_CHECK_CLEAN_TOPIC for NoFraudEvent messages. When a message is received,
     * it extracts the reference number from the payload and calls the processCleanTransaction method of the transactionService to handle the clean transaction.
     * @param payload
     */
    @KafkaListener(topics = Topic.FRAUD_CHECK_CLEAN_TOPIC, groupId = ConsumerGroup.FRAUD_CHECK_CLEAN_CONSUMER_GROUP)
    public void handleFraudCheckCleanEvent(@Payload NoFraudEvent payload) {
        String referenceNumber = payload.getReferenceNumber();
        transactionService.processCleanTransaction(referenceNumber);
    }
}
