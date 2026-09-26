package com.banking.postservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericKafkaProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;


	public CompletableFuture<SendResult<String, Object>> publish(
			String topic,
			String key,
			Object event
	) {

		log.info(
				"Publishing Kafka event. topic={}, key={}",
				topic,
				key
		);

		return kafkaTemplate.send(
				topic,
				key,
				event
		);
	}
}