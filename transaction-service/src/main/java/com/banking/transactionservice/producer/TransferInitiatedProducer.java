package com.banking.transactionservice.producer;

import com.banking.transactionservice.event.TransactionInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferInitiatedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTransactionInitiatedEvent(String topicName, String key, TransactionInitiatedEvent event) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topicName, key, event);
        producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        kafkaTemplate.send(producerRecord)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send product creation event to topic: {}", topicName, exception);
                    } else {
                        log.info("Product creation event sent successfully to topic: {}", topicName);
                    }
                });
    }
}
