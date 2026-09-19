package com.yash.banking_transaction_service.exceptions.account;

public class InsufficientAvailableBalanceException extends RuntimeException {
    public InsufficientAvailableBalanceException() {
        super("Insufficient balance");
    }
}
