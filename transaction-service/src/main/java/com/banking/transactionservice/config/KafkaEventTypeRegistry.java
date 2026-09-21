package com.banking.transactionservice.config;

import com.banking.transactionservice.constants.Topic;
import com.banking.transactionservice.event.CleanTransactionEvent;
import com.banking.transactionservice.event.VerificationRequiredEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaEventTypeRegistry {

	private final Map<String, Class<?>> eventTypes = new HashMap<>();

	public KafkaEventTypeRegistry() {

		eventTypes.put(
				Topic.VERIFICATION_REQUIRED,
				VerificationRequiredEvent.class
		);

		eventTypes.put(
				Topic.FRAUD_CHECK_CLEAN_TOPIC,
				CleanTransactionEvent.class
		);

		// Future events
		// eventTypes.put("transaction.completed", TransactionCompletedEvent.class);
		// eventTypes.put("fraud.detected", FraudDetectedEvent.class);
	}

	public Class<?> getEventClass(String topic) {

		Class<?> eventClass = eventTypes.get(topic);

		if (eventClass == null) {
			throw new IllegalArgumentException(
					"No event mapping configured for topic: " + topic
			);
		}

		return eventClass;
	}
}
