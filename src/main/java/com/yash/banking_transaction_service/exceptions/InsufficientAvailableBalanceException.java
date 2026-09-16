package com.yash.banking_transaction_service.exceptions;

public class InsufficientAvailableBalanceException extends RuntimeException {
    public InsufficientAvailableBalanceException() {
        super("Insufficient balance");
    }
}
