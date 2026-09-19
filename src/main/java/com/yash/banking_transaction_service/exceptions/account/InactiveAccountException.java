package com.yash.banking_transaction_service.exceptions.account;

public class InactiveAccountException extends RuntimeException {
    public InactiveAccountException(String accountType, String accountNumber) {
        super(accountType + " account with account number " + accountNumber + " must be active");
    }
}
