package com.banking.transactionservice.constants;

public class ConsumerGroup {
	public static final String TRANSACTION_INITIATED_CONSUMER_GROUP = "transaction-initiated-consumer-group";
	public static final String TRANSACTION_COMPLETED_CONSUMER_GROUP = "transaction-completed-consumer-group";
	public static final String TRANSACTION_REFUNDED_CONSUMER_GROUP = "transaction-refunded-consumer-group";
	public static final String VERIFICATION_OTP_GENERATED_CONSUMER_GROUP = "verification-otp-generated-consumer-group";
	public static final String FRAUD_DETECTED_CONSUMER_GROUP = "fraud-detected-consumer-group";
	public static final String FRAUD_CHECK_CLEAN_CONSUMER_GROUP = "fraud-check-clean-consumer-group";
}
