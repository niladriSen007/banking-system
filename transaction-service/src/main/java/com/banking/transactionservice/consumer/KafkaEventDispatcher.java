package com.banking.transactionservice.consumer;

import com.banking.transactionservice.constants.Topic;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaEventDispatcher {

	private final Map<String, KafkaEventHandler<?>> handlers =
			new HashMap<>();

	public KafkaEventDispatcher(
			VerificationRequiredEventHandler verificationHandler,
			FraudCheckCleanEventHandler fraudCheckCleanHandler) {

		handlers.put(
				Topic.VERIFICATION_REQUIRED,
				verificationHandler
		);

		handlers.put(
				Topic.FRAUD_CHECK_CLEAN_TOPIC,
				fraudCheckCleanHandler
		);
	}

	public KafkaEventHandler<?> getHandler(String topic) {

		KafkaEventHandler<?> handler =
				handlers.get(topic);

		if (handler == null) {
			throw new IllegalArgumentException(
					"No handler registered for topic: " + topic
			);
		}

		return handler;
	}
}