package com.banking.notificationservice.consumer;

import com.banking.notificationservice.constants.Topic;
import com.banking.notificationservice.event.AmountRefundEvent;
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
public class AmountRefundEventConsumer {

	@KafkaListener(topics = Topic.TRANSACTION_REFUNDED_TOPIC, groupId = "transaction-refund-group")
	public void handleAmountRefundEvent(@Payload AmountRefundEvent amountRefundEvent) {

		try {
			Long senderAccountNumber = amountRefundEvent.getSenderAccountNumber();
			String reason = amountRefundEvent.getReason();
			BigDecimal amount = amountRefundEvent.getAmount();

			sendAlert(senderAccountNumber.toString(), "REFUND PROCESSED",
					String.format("Your transaction of amount %s was cancelled." +
							"Reason: %s" +
							"amount %s has been refunded to your account %s", amount, reason, amount, senderAccountNumber));
		} catch (Exception e) {
			log.error("Exception occurred while processing amountRefundEvent", e);
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
