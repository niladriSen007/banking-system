package com.banking.frauddetectionservice.consumers;

import com.banking.frauddetectionservice.events.TransactionInitiatedEvent;
import com.banking.frauddetectionservice.services.IFraudDetectionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FraudDetectionEventConsumer {

    IFraudDetectionService fraudDetectionService;

    /**
     * Consume the Transaction initiated event
     * Checks the transaction is fraud or not
     *
     * @param payload
     */
    @KafkaListener(topics = "transaction.initiated",groupId = "fraud-detection-service-group")
    public void handleTransactionInitiatedEvent(@Payload TransactionInitiatedEvent payload) {

        log.info("Received transaction initiated event for transaction with reference number: {}", payload.getReferenceNumber());
        fraudDetectionService.checkTransactionFraud(payload);
    }
}
