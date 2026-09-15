package com.yash.banking_transaction_service.exceptions;

public class AccountStateException extends RuntimeException {
    public AccountStateException(String message) {
        super(message);
    }
}
