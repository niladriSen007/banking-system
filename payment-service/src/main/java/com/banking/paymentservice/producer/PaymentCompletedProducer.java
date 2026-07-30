package com.banking.paymentservice.producer;

import com.banking.paymentservice.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCompletedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompletedEvent(String topicName, String key, PaymentCompletedEvent event) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topicName, key, event);
        producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        kafkaTemplate.send(producerRecord)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send payment completed event to topic: {}", topicName, exception);
                    } else {
                        log.info("Payment completed event sent successfully to topic: {}", topicName);
                    }
                });
    }
}
