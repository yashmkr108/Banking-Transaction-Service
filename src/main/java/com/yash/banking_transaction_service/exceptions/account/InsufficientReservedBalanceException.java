package com.yash.banking_transaction_service.exceptions.account;

public class InsufficientReservedBalanceException extends RuntimeException {
    public InsufficientReservedBalanceException() {
        super("Insufficient reserved balance for settlement");
    }
}
