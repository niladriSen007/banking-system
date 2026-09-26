package com.banking.connectionservice.exception;

public class ConnectionRequestAlreadyExists extends RuntimeException {
	public ConnectionRequestAlreadyExists(String message) {
		super(message);
	}
}
