package com.banking.transactionservice.consumer;

import com.banking.transactionservice.config.KafkaEventTypeRegistry;
import com.banking.transactionservice.constants.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenericKafkaConsumer {

	private final KafkaEventDispatcher kafkaEventDispatcher;

	private final KafkaEventTypeRegistry eventTypeRegistry;
	private final ObjectMapper objectMapper;

	@KafkaListener(
			topics = {
					Topic.VERIFICATION_REQUIRED,
					Topic.FRAUD_CHECK_CLEAN_TOPIC,
			},
			groupId = "transaction-service-group"
	)
	public void consume(
			ConsumerRecord<String, String> record) {

		log.info(
				"Received Kafka message. topic={}, partition={}, offset={}",
				record.topic(),
				record.partition(),
				record.offset()
		);

		String rawPayload = record.value();

		log.info("Raw payload: {}", rawPayload);

		KafkaEventHandler<?> handler =
				kafkaEventDispatcher.getHandler(record.topic());

		if (handler == null) {
			throw new IllegalStateException(
					"No handler registered for topic: " + record.topic()
			);
		}

		Class<?> eventType = handler.getEventType();

		try {

			JsonNode jsonNode =
					objectMapper.readTree(rawPayload);

			Object event =
					objectMapper.treeToValue(
							jsonNode,
							eventType
					);

			log.info(
					"Deserialized event. topic={}, eventType={}, event={}",
					record.topic(),
					eventType.getSimpleName(),
					event
			);

			invokeHandler(handler, event);

		} catch (Exception e) {

			log.error(
					"Failed to deserialize Kafka event. topic={}, eventType={}, payload={}",
					record.topic(),
					eventType.getSimpleName(),
					rawPayload,
					e
			);

			throw new RuntimeException(
					"Failed to deserialize Kafka event",
					e
			);
		}


	}


	@SuppressWarnings("unchecked")
	private void invokeHandler(
			KafkaEventHandler<?> handler,
			Object event) {

		((KafkaEventHandler<Object>) handler).handle(event);
	}
}