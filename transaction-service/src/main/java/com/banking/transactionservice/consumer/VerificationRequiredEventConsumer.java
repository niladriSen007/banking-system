package com.banking.transactionservice.consumer;

import com.banking.transactionservice.constants.Topic;
import com.banking.transactionservice.entity.Transaction;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.event.SendOtpEvent;
import com.banking.transactionservice.event.VerificationRequiredEvent;
import com.banking.transactionservice.exceptions.TransactionNotFoundException;
import com.banking.transactionservice.producer.SendOtpProducer;
import com.banking.transactionservice.repository.TransactionRepository;
import com.banking.transactionservice.service.ITransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VerificationRequiredEventConsumer {

//    ITransactionService transactionService;
    TransactionRepository transactionRepository;
    RedisTemplate<String, String> redisTemplate;
    SendOtpProducer sendOtpProducer;
    static long OTP_EXPIRY_MINUTES = 5;

    @KafkaListener(topics = "verification.required", groupId = "verification-required-group")
    public void handleVerificationRequiredEvent(@Payload VerificationRequiredEvent payload) {
        log.info("Verification required - transaction: {} reason: {}",
                payload.getReferenceNumber(), payload.getReason());

        Transaction transaction = transactionRepository.findByReferenceNumber(payload.getReferenceNumber())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (transaction.getTransactionStatus() != TransactionStatus.PROCESSING) {
            log.warn("Transaction {} not PROCESSING - skipping", payload.getReferenceNumber());
            return;
        }

        // Generate 6 digit otp
        String otp = String.format("%06d", (int) (Math.random() * 900000) + 100000);

        // Store OTP in Redis - expires in 5 minutes
        String otpKey = "verification:otp" + payload.getReferenceNumber();
        redisTemplate.opsForValue().set(otpKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

        // Update Status
        transaction.setTransactionStatus(TransactionStatus.PENDING_VERIFICATION);
        transactionRepository.save(transaction);

        log.info("OTP generated for transaction: {} expires in {} min",
                payload.getReferenceNumber(), OTP_EXPIRY_MINUTES);


        SendOtpEvent otpEvent = SendOtpEvent.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .otp(otp)
                .senderAccountNumber(transaction.getSenderAccountNumber())
                .reason(payload.getReason())
                .amount(payload.getAmount())
                .build();

        sendOtpProducer.publishSendOtpEvent(
                Topic.VERIFICATION_OTP_GENERATED_TOPIC,
                transaction.getReferenceNumber(),
                otpEvent);
    }
}
