package com.banking.notificationservice.consumer;

import com.banking.notificationservice.constants.Topic;
import com.banking.notificationservice.event.PaymentCompletedEvent;
import com.banking.notificationservice.event.PaymentFailedEvent;
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
public class PaymentFailedEventConsumer {

	@KafkaListener(topics = Topic.PAYMENT_FAILED_TOPIC, groupId = "payment-failed-group")
	public void handleOtpGeneratedEvent(@Payload PaymentFailedEvent paymentFailedEvent) {
		log.info("Payment Failed event {}", paymentFailedEvent);

		try {
			Long senderAccountNumber = paymentFailedEvent.getAccountNumber();
			BigDecimal amount = paymentFailedEvent.getAmount();
			String reason = paymentFailedEvent.getReason();

			sendAlert(senderAccountNumber.toString(),
					"❌ PAYMENT FAILED",
					String.format(
							"Payment of amount %s from account: %s failed" +
									"Reason: %s", amount, senderAccountNumber, reason));
		} catch (Exception e) {
			log.error("Error sending payment notification: {}", e.getMessage());
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
