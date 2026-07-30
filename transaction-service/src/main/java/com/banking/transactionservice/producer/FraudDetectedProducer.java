package com.banking.transactionservice.producer;

import com.banking.transactionservice.event.AmountRefundEvent;
import com.banking.transactionservice.event.FraudDetectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDetectedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishFraudDetectedEvent(String topicName, String key, FraudDetectedEvent event) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topicName, key, event);
        producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        kafkaTemplate.send(producerRecord)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send fraud detected event to topic: {}", topicName, exception);
                    } else {
                        log.info("Fraud detected event sent successfully to topic: {}", topicName);
                    }
                });
    }
}
