package com.banking.notificationservice.consumer;

import com.banking.notificationservice.constants.Topic;
import com.banking.notificationservice.event.TransactionCompletedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionCompletedEventConsumer {

	@KafkaListener(topics = Topic.TRANSACTION_COMPLETED_TOPIC, groupId = "transaction-completed-group")
	public void handleTransactionCompletedEvent(@Payload TransactionCompletedEvent transactionCompletedEvent) {

		// TODO: Handle exception try/catch scenario
		log.info("Received Transaction Completed event {}", transactionCompletedEvent);

		Long senderAccountNumber = transactionCompletedEvent.getSenderAccountNumber();
		BigDecimal amount = transactionCompletedEvent.getAmount();
		Long receiverAccountNumber = transactionCompletedEvent.getReceiverAccountNumber();

		// DEBIT from sender
		sendAlert(senderAccountNumber.toString(), "DEBIT ALERT", String.format("%s debited from account no: %s", amount, senderAccountNumber));

		// CREDIT TO receiver
		sendAlert(receiverAccountNumber.toString(), "CREDIT ALERT", String.format("%s credited from account no: %s", amount, receiverAccountNumber));
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
