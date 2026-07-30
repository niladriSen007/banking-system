package com.banking.notificationservice.consumer;

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
public class OtpGeneratedEventConsumer {

    @KafkaListener(topics = "transaction.otp.generated", groupId = "otp-generated-group")
    public void handleOtpGeneratedEvent(@Payload SendOtpEvent sendOtpEvent) {
        log.info("Received OTP event {}", sendOtpEvent);

        Long senderAccountNumber = sendOtpEvent.getSenderAccountNumber();
        BigDecimal amount = sendOtpEvent.getAmount();
        String otp = sendOtpEvent.getOtp();
        String transactionReferenceNumber = sendOtpEvent.getReferenceNumber();
        String reason = sendOtpEvent.getReason();

        sendAlert(senderAccountNumber.toString(),
                "🔐 TRANSACTION VERIFICATION REQUIRED",
                String.format(
                        "Suspicious activity detected on your account. " +
                                "Reason: %s. " +
                                "A transaction of ₹%s is pending verification. " +
                                "Your OTP is: %s. Valid for 5 minutes. " +
                                "If this wasn't you — ignore this message. " +
                                "Transaction will be cancelled and amount refunded automatically.",
                        reason, amount, otp, transactionReferenceNumber, otp));
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
