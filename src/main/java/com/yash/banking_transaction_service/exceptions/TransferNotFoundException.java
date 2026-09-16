package com.yash.banking_transaction_service.exceptions;

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(String reference) {
        super(
                "Transfer not found : " + reference
        );
    }
}
