package com.banking.connectionservice.exception;

public class NotAuthorisedToAcceptConnectionRequest extends RuntimeException {
	public NotAuthorisedToAcceptConnectionRequest(String message) {
		super(message);
	}
}
