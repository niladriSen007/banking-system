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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationRequiredEventHandler
		implements KafkaEventHandler<VerificationRequiredEvent> {

	private final TransactionRepository transactionRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final SendOtpProducer sendOtpProducer;
	private static final long OTP_EXPIRY_MINUTES = 5;

	@Override
	public void handle(VerificationRequiredEvent verificationEvent) {

		log.info(
				"Processing verification required: {}",
				verificationEvent
		);

		log.info("Verification required - transaction: {} reason: {}",
				verificationEvent.getReferenceNumber(), verificationEvent.getReason());

		Transaction transaction = transactionRepository.findByReferenceNumber(verificationEvent.getReferenceNumber())
				.orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

		if (transaction.getTransactionStatus() != TransactionStatus.PROCESSING) {
			log.warn("Transaction {} not PROCESSING - skipping", verificationEvent.getReferenceNumber());
			return;
		}

		// Generate 6 digit otp
		String otp = String.format("%06d", (int) (Math.random() * 900000) + 100000);

		// Store OTP in Redis - expires in 5 minutes
		String otpKey = "verification:otp-" + verificationEvent.getReferenceNumber();
		redisTemplate.opsForValue().set(otpKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

		// Update Status
		transaction.setTransactionStatus(TransactionStatus.PENDING_VERIFICATION);
		transactionRepository.save(transaction);

		log.info("OTP generated for transaction: {} expires in {} min",
				verificationEvent.getReferenceNumber(), OTP_EXPIRY_MINUTES);


		SendOtpEvent otpEvent = SendOtpEvent.builder()
				.referenceNumber(transaction.getReferenceNumber())
				.otp(otp)
				.senderAccountNumber(transaction.getSenderAccountNumber())
				.reason(verificationEvent.getReason())
				.amount(verificationEvent.getAmount())
				.build();

		sendOtpProducer.publishSendOtpEvent(
				Topic.VERIFICATION_OTP_GENERATED_TOPIC,
				transaction.getReferenceNumber(),
				otpEvent);
	}

	@Override
	public Class<VerificationRequiredEvent> getEventType() {
		return VerificationRequiredEvent.class;
	}
}
