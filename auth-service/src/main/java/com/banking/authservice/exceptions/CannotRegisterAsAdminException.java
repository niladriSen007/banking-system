package com.banking.authservice.exceptions;

public class CannotRegisterAsAdminException extends RuntimeException {
	public CannotRegisterAsAdminException(String message) {
		super(message);
	}
}
