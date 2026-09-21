package com.banking.transactionservice.consumer;

import com.banking.transactionservice.constants.ConsumerGroup;
import com.banking.transactionservice.constants.Topic;
import com.banking.transactionservice.event.CleanTransactionEvent;
import com.banking.transactionservice.event.VerificationRequiredEvent;
import com.banking.transactionservice.service.ITransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
@RequiredArgsConstructor
public class FraudCheckCleanEventHandler
		implements KafkaEventHandler<CleanTransactionEvent> {

	private final ITransactionService transactionService;
	private final ObjectMapper objectMapper;

	@Override
	public void handle(CleanTransactionEvent cleanTransactionEvent) {

		log.info(
				"Processing clean transaction: {}",
				cleanTransactionEvent
		);

		String referenceNumber = cleanTransactionEvent.getReferenceNumber();
		transactionService.processCleanTransaction(referenceNumber);
	}

	@Override
	public Class<CleanTransactionEvent> getEventType() {
		return CleanTransactionEvent.class;
	}
}
