package com.banking.postservice.exception;

public class PostNotFoundException
		extends RuntimeException {

	public PostNotFoundException(String message) {
		super(message);
	}
}