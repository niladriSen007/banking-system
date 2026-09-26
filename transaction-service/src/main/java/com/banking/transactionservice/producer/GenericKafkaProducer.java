package com.banking.transactionservice.producer;

import com.banking.transactionservice.event.FraudDetectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericKafkaProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public <T> void publish(
			String topic,
			String key,
			T event
	) {
		ProducerRecord<String, Object> record =
				new ProducerRecord<>(topic, key, event);

		record.headers().add(
				"messageId",
				UUID.randomUUID()
						.toString()
						.getBytes(StandardCharsets.UTF_8)
		);

		kafkaTemplate.send(record)
				.whenComplete((result, exception) -> {

					if (exception != null) {
						log.error(
								"Failed to publish event. type={}, topic={}",
								event.getClass().getSimpleName(),
								topic,
								exception
						);
					} else {
						log.info(
								"Published event. type={}, topic={}, partition={}, offset={}",
								event.getClass().getSimpleName(),
								topic,
								result.getRecordMetadata().partition(),
								result.getRecordMetadata().offset()
						);
					}
				});
	}
}
