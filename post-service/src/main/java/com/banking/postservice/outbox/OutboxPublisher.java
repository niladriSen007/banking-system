package com.banking.postservice.outbox;

import com.banking.postservice.entity.OutboxEvent;
import com.banking.postservice.kafka.GenericKafkaProducer;
import com.banking.postservice.repository.OutboxEventRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

	private final OutboxEventRepository outboxEventRepository;

	private final GenericKafkaProducer kafkaProducer;

	private final ObjectMapper objectMapper;


	@Scheduled(
			initialDelayString = "${outbox.publisher.initial-delay-ms:15000}",
			fixedDelayString = "${outbox.publisher.delay-ms:5000}"
	)
	public void publishEvents() {

		LocalDate today =
				LocalDate.now(ZoneOffset.UTC);

		List<OutboxEvent> events =
				outboxEventRepository
						.findByKeyBucketDateAndStatus(
								today,
								"NEW"
						);

		if (events.isEmpty()) {
			return;
		}

		log.info(
				"Found {} NEW outbox events",
				events.size()
		);

		for (OutboxEvent event : events) {

			publishEvent(event);
		}
	}


	private void publishEvent(
			OutboxEvent event
	) {

		try {

			event.setLastAttemptAt(
					Instant.now()
			);

			event.setRetryCount(
					event.getRetryCount() + 1
			);

			outboxEventRepository.save(event);


			JsonNode payload =
					objectMapper.readTree(
							event.getPayload()
					);


			kafkaProducer
					.publish(
							event.getEventType(),
							event.getKey()
									.getEventId()
									.toString(),
							payload
					)
					.whenComplete(
							(result, exception) -> {

								if (exception != null) {

									handlePublishFailure(
											event,
											exception
									);

									return;
								}

								handlePublishSuccess(
										event
								);
							}
					);


		} catch (Exception exception) {

			log.error(
					"Error processing outbox event. eventId={}",
					event.getKey().getEventId(),
					exception
			);

			markFailed(
					event,
					exception
			);
		}
	}


	private void handlePublishSuccess(
			OutboxEvent event
	) {

		event.setStatus("PUBLISHED");

		event.setPublishedAt(
				Instant.now()
		);

		event.setLastError(null);

		outboxEventRepository.save(event);

		log.info(
				"Outbox event published successfully. eventId={}, topic={}",
				event.getKey().getEventId(),
				event.getEventType()
		);
	}


	private void handlePublishFailure(
			OutboxEvent event,
			Throwable exception
	) {

		markFailed(
				event,
				exception
		);
	}


	private void markFailed(
			OutboxEvent event,
			Throwable exception
	) {

		event.setStatus("FAILED");

		event.setLastError(
				exception.getMessage()
		);

		outboxEventRepository.save(event);

		log.error(
				"Outbox event failed. eventId={}, retryCount={}",
				event.getKey().getEventId(),
				event.getRetryCount(),
				exception
		);
	}
}