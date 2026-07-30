package com.banking.transactionservice.producer;

import com.banking.transactionservice.event.AmountRefundEvent;
import com.banking.transactionservice.event.SendOtpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmountRefundProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAmountRefundEvent(String topicName, String key, AmountRefundEvent event) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topicName, key, event);
        producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        kafkaTemplate.send(producerRecord)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send amount refund event to topic: {}", topicName, exception);
                    } else {
                        log.info("Amount refund event sent successfully to topic: {}", topicName);
                    }
                });
    }
}
