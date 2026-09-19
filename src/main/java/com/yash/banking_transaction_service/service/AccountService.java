package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.dto.AccountResponse;
import com.yash.banking_transaction_service.dto.CreateAccountRequest;
import com.yash.banking_transaction_service.dto.DepositRequest;
import com.yash.banking_transaction_service.entity.Account;
import com.yash.banking_transaction_service.enums.AccountStatus;
import com.yash.banking_transaction_service.exceptions.account.AccountNotFoundException;
import com.yash.banking_transaction_service.exceptions.account.AccountStateException;
import com.yash.banking_transaction_service.exceptions.transfer.generator.AccountNumberGenerator;
import com.yash.banking_transaction_service.mapper.AccountMapper;
import com.yash.banking_transaction_service.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        String accountNumber = accountNumberGenerator.generate();
        Account account = new Account(request.ownerName(), accountNumber);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    public AccountResponse getByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return accountMapper.toResponse(account);
    }

    public AccountResponse closeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException("Account is already closed");
        }

        account.close();
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    public AccountResponse activateAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException("Closed account cannot be activated");
        }

        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AccountStateException("Account is already active");
        }

        account.activate();
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    public AccountResponse blockAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException("Closed account cannot be blocked ");
        }

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new AccountStateException("Account is already blocked");
        }

        account.block();
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    @Transactional
    public AccountResponse deposit(String accountNumber, DepositRequest request) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStateException("Only active accounts can receive deposits");
        }
        account.deposit(request.amount());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

}
