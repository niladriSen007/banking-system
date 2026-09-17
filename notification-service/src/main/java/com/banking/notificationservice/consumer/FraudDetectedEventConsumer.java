package com.banking.notificationservice.consumer;

import com.banking.notificationservice.constants.Topic;
import com.banking.notificationservice.event.FraudDetectedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FraudDetectedEventConsumer {


	@KafkaListener(topics = Topic.FRAUD_DETECTED_TOPIC, groupId = "fraud-detected-group")
	public void handleFraudDetectedEvent(@Payload FraudDetectedEvent fraudDetectedEvent) {
		try {
			Long senderAccountNumber = fraudDetectedEvent.getSenderAccountNumber();
			String reason = fraudDetectedEvent.getReason();
//			String referenceNumber = fraudDetectedEvent.getReferenceNumber();
			sendAlert(senderAccountNumber.toString(), "SUSPICIOUS ACTIVITY DETECTED",
					String.format("Your account with account no : %s has been temporarily blocked. Reason: %s", senderAccountNumber, reason));
		} catch (Exception e) {
			log.error("Fraud detected event consume failed");
		}
	}

	private void sendAlert(String accountNumber,
	                       String subject,
	                       String message) {
		log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		log.info("NOTIFICATION SENT");
		log.info("Account : {}", accountNumber);
		log.info("Subject : {}", subject);
		log.info("Message : {}", message);
		log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
	}
}
