package com.banking.transactionservice.consumer;

import com.fasterxml.jackson.databind.JsonNode;

public interface KafkaEventHandler<T> {
	void handle(T event);

	Class<T> getEventType();
}
