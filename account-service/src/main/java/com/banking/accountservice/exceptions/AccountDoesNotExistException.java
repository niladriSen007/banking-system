package com.banking.accountservice.exceptions;

public class AccountDoesNotExistException extends RuntimeException{
    public AccountDoesNotExistException(String message) {
        super(message);
    }
}
