package com.banking.frauddetectionservice.consumers;

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



    /**
     * Consume the Transaction initiated event
     * Checks the transaction is fraud or not
     *
     * @param payload
     */
    @KafkaListener(topics = "transaction.initiated",groupId = "fraud-detection-service-group")
    public void handleTransactionInitiatedEvent(@Payload Map<String, Object> payload) {
        String transactionReferenceNumber = payload.get("transactionReferenceNumber").toString();

        log.info("Received transaction initiated event for transaction with reference number: {}", transactionReferenceNumber);
        accountService.creditBalnce(recipientAccountNumber, amount);
    }
}
