package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.dto.AccountResponse;
import com.yash.banking_transaction_service.dto.CreateAccountRequest;
import com.yash.banking_transaction_service.dto.DepositRequest;
import com.yash.banking_transaction_service.entity.Account;
import com.yash.banking_transaction_service.enums.AccountStatus;
import com.yash.banking_transaction_service.enums.AuditAction;
import com.yash.banking_transaction_service.enums.AuditStatus;
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
    private final AuditService auditService;

    public AccountService(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            AccountNumberGenerator accountNumberGenerator,
            AuditService auditService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountNumberGenerator = accountNumberGenerator;
        this.auditService = auditService;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        String accountNumber = accountNumberGenerator.generate();
        Account account = new Account(request.ownerName(), accountNumber, request.email());
        Account savedAccount = accountRepository.save(account);

        auditService.record(
                AuditAction.ACCOUNT_CREATED,
                account.getAccountNumber(),
                AuditStatus.SUCCESS,
                "Account created successfully"
        );

        return accountMapper.toResponse(savedAccount);
    }

    public AccountResponse getByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse closeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException("Account is already closed");
        }

        account.close();

        auditService.record(
                AuditAction.ACCOUNT_CLOSED,
                account.getAccountNumber(),
                AuditStatus.SUCCESS,
                "Account closed successfully"
        );

        return accountMapper.toResponse(account);
    }

    @Transactional
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

        auditService.record(
                AuditAction.ACCOUNT_ACTIVATED,
                account.getAccountNumber(),
                AuditStatus.SUCCESS,
                "Account activated successfully"
        );

        return accountMapper.toResponse(account);
    }

    @Transactional
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

        auditService.record(
                AuditAction.ACCOUNT_BLOCKED,
                account.getAccountNumber(),
                AuditStatus.SUCCESS,
                "Account blocked successfully"
        );

        return accountMapper.toResponse(account);
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
