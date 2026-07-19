package com.banking.accountservice.exceptions;

public class AccountNotActiveForTransactionsException extends RuntimeException {
    public AccountNotActiveForTransactionsException(String message) {
        super(message);
    }
}
