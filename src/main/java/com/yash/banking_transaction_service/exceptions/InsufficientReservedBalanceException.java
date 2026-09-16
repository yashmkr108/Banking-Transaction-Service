package com.yash.banking_transaction_service.exceptions;

public class InsufficientReservedBalanceException extends RuntimeException {
    public InsufficientReservedBalanceException() {
        super("Insufficient reserved balance for settlement");
    }
}
