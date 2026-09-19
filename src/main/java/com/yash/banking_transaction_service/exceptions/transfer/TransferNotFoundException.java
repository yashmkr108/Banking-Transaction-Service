package com.yash.banking_transaction_service.exceptions.transfer;

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(String reference) {
        super(
                "Transfer not found : " + reference
        );
    }
}
