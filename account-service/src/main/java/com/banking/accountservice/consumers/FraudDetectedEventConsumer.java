package com.banking.accountservice.consumers;

import com.banking.accountservice.service.IAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FraudDetectedEventConsumer {

	private final IAccountService accountService;

	/**
	 * Consume the Fraud Detection event
	 * Blocks the account with the target accountNumber
	 *
	 * @param payload
	 */
	@KafkaListener(topics = "fraud.detected",groupId = "fraud-detection-service-group")
	public void handleFraudDetectedEvent(@Payload Map<String, Object> payload) {
		String recipientAccountNumber = payload.get("recipientAccountNumber").toString();

		log.info("DetectedFraud so Blocking the account with account number {}", recipientAccountNumber);
		accountService.blockAccount(recipientAccountNumber);

	}
}
