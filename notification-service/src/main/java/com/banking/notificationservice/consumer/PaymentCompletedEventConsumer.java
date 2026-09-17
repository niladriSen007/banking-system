package com.banking.notificationservice.consumer;

import com.banking.notificationservice.constants.Topic;
import com.banking.notificationservice.event.PaymentCompletedEvent;
import com.banking.notificationservice.event.SendOtpEvent;
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
public class PaymentCompletedEventConsumer {

	@KafkaListener(topics = Topic.PAYMENT_COMPLETED_TOPIC, groupId = "payment-completed-group")
	public void handleOtpGeneratedEvent(@Payload PaymentCompletedEvent paymentCompletedEvent) {
		log.info("Payment Completed event {}", paymentCompletedEvent);

		try {
			Long senderAccountNumber = paymentCompletedEvent.getAccountNumber();
			BigDecimal amount = paymentCompletedEvent.getAmount();
			String razorpayPaymentId = paymentCompletedEvent.getRazorpayPaymentId();

			sendAlert(senderAccountNumber.toString(),
					"✔️ PAYMENT SUCCESSFUL",
					String.format(
							"Payment of amount %s from account: %s is completed" +
									"Razorpay ID : %s", amount, senderAccountNumber, razorpayPaymentId));
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
