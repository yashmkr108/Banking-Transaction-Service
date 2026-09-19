package com.yash.banking_transaction_service.exceptions.account;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account not found with account number : " + accountNumber);
    }

    public AccountNotFoundException() {
        super("Account not found");
    }
}
