package com.banking.transactionservice.entity;


/**
 * Transaction LifeCycle ->
 *
 *      PENDING -> PROCESSING -> COMPLETED
 *                            -> PENDING_VERIFICATION(Suspicious)
 *                                          -> COMPLETED
 *                                          -> FLAGGED(Refund)
 *                            -> FAILED
 *                            -> FLAGGED
 */

public enum TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    PENDING_VERIFICATION,
    FLAGGED,
    FAILED
}
