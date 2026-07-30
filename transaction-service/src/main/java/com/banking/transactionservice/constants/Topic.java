package com.banking.transactionservice.constants;

public class Topic {
    public static final String TRANSACTION_INITIATED_TOPIC = "transaction.initiated";
    public static final String TRANSACTION_COMPLETED_TOPIC = "transaction.completed";
    public static final String TRANSACTION_REFUNDED_TOPIC = "transaction.refunded";
    public static final String VERIFICATION_OTP_GENERATED_TOPIC = "transaction.otp.generated";
    public static final String FRAUD_DETECTED_TOPIC = "fraud.detected";
    public static final String FRAUD_CHECK_CLEAN_TOPIC = "fraud.check.clean";
}
