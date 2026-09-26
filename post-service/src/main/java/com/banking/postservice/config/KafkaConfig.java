package com.banking.postservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Bean
	public ProducerFactory<String, Object> producerFactory() {

		Map<String, Object> props = new HashMap<>();

		props.put(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				"localhost:9092"
		);

		props.put(
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class
		);

		props.put(
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				JacksonJsonSerializer.class
		);

		props.put(
				ProducerConfig.ACKS_CONFIG,
				"all"
		);

		props.put(
				ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
				true
		);

		props.put(
				ProducerConfig.RETRIES_CONFIG,
				5
		);

		props.put(
				ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
				5
		);

		return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate(
			ProducerFactory<String, Object> producerFactory
	) {
		return new KafkaTemplate<>(producerFactory);
	}
}