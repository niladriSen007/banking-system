package com.banking.transactionservice.exceptions;

public class BulkheadExceededException extends RuntimeException {
	public BulkheadExceededException(String message) {
		super(message);
	}
}
