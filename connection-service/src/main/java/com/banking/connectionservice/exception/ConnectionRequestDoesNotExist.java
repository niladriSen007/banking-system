package com.banking.connectionservice.exception;

public class ConnectionRequestDoesNotExist extends RuntimeException {
	public ConnectionRequestDoesNotExist(String message) {
		super(message);
	}
}
