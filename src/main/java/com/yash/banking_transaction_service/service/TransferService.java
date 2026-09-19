package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.entity.Account;
import com.yash.banking_transaction_service.entity.IdempotencyRecord;
import com.yash.banking_transaction_service.entity.LedgerEntry;
import com.yash.banking_transaction_service.entity.Transfer;
import com.yash.banking_transaction_service.enums.AccountStatus;
import com.yash.banking_transaction_service.enums.LedgerEntryType;
import com.yash.banking_transaction_service.enums.TransferStatus;
import com.yash.banking_transaction_service.exceptions.account.AccountNotFoundException;
import com.yash.banking_transaction_service.exceptions.account.InactiveAccountException;
import com.yash.banking_transaction_service.exceptions.transfer.InvalidTransferStatusException;
import com.yash.banking_transaction_service.exceptions.transfer.SameAccountTransferException;
import com.yash.banking_transaction_service.exceptions.transfer.TransferExecutionException;
import com.yash.banking_transaction_service.exceptions.transfer.TransferNotFoundException;
import com.yash.banking_transaction_service.exceptions.transfer.generator.RequestFingerprintGenerator;
import com.yash.banking_transaction_service.exceptions.transfer.generator.TransferReferenceGenerator;
import com.yash.banking_transaction_service.mapper.TransferMapper;
import com.yash.banking_transaction_service.repository.AccountRepository;
import com.yash.banking_transaction_service.repository.LedgerEntryRepository;
import com.yash.banking_transaction_service.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final TransferReferenceGenerator transferReferenceGenerator;
    private final TransferMapper transferMapper;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final IdempotencyService idempotencyService;
    private final RequestFingerprintGenerator fingerprintGenerator;

    public TransferService(
            AccountRepository accountRepository,
            TransferRepository transferRepository,
            TransferReferenceGenerator transferReferenceGenerator,
            TransferMapper transferMapper,
            LedgerEntryRepository ledgerEntryRepository,
            IdempotencyService idempotencyService,
            RequestFingerprintGenerator fingerprintGenerator
    ) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.transferReferenceGenerator = transferReferenceGenerator;
        this.transferMapper = transferMapper;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.idempotencyService = idempotencyService;
        this.fingerprintGenerator = fingerprintGenerator;
    }

    @Transactional
    public TransferResponse createTransfer(CreateTransferRequest request,String idempotencyKey) {

        String fingerprint = fingerprintGenerator.generate(request);

        IdempotencyRecord idempotencyRecord =
                idempotencyService.createRecord(
                        idempotencyKey,
                        fingerprint
                );

        String sourceAccountNumber = request.sourceAccountNumber();
        String destinationAccountNumber = request.destinationAccountNumber();
        BigDecimal amount = request.amount();

        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new SameAccountTransferException();
        }

        Account sourceAccount = accountRepository.findByAccountNumberForUpdate(sourceAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(sourceAccountNumber));
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(destinationAccountNumber));

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException("Source", sourceAccountNumber);
        }
        if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException("Destination", destinationAccountNumber);
        }

        sourceAccount.reserve(amount);

        String reference = transferReferenceGenerator.generate();

        Transfer transfer = new Transfer(reference, sourceAccount, destinationAccount, amount);

        Transfer savedTransfer = transferRepository.save(transfer);

        idempotencyRecord.attachTransfer(savedTransfer.getReference());

        return transferMapper.toResponse(savedTransfer);
    }

    @Transactional
    public TransferResponse executeTransfer(String reference) {

        Transfer transfer = transferRepository.findByReferenceForUpdate(reference)
                .orElseThrow(() -> new TransferNotFoundException(reference));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new InvalidTransferStatusException("Transfer status must be PENDING");
        }

        Long sourceAccountId = transfer.getSourceAccount().getId();
        Long destinationAccountId = transfer.getDestinationAccount().getId();

        Long firstAccountId = Math.min(sourceAccountId, destinationAccountId);
        Long secondAccountId = Math.max(sourceAccountId, destinationAccountId);

        Account firstAccount = accountRepository.findByIdForUpdate(firstAccountId)
                .orElseThrow(AccountNotFoundException::new);

        Account secondAccount = accountRepository.findByIdForUpdate(secondAccountId)
                .orElseThrow(AccountNotFoundException::new);

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new InvalidTransferStatusException("Transfer status must be PENDING");
        }

        Account sourceAccount;
        Account destinationAccount;

        if (firstAccount.getId().equals(sourceAccountId)) {
            sourceAccount = firstAccount;
            destinationAccount = secondAccount;
        } else {
            sourceAccount = secondAccount;
            destinationAccount = firstAccount;
        }

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new TransferExecutionException("Transfer cannot be executed because source account must be active"
            );
        }
        if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new TransferExecutionException("Transfer cannot be executed because destination account must be active"
            );
        }

        sourceAccount.settleDebit(transfer.getAmount());
        destinationAccount.settleCredit(transfer.getAmount());

        LedgerEntry debitEntry = new LedgerEntry(transfer, sourceAccount, LedgerEntryType.DEBIT, transfer.getAmount());
        LedgerEntry creditEntry = new LedgerEntry(transfer, destinationAccount, LedgerEntryType.CREDIT, transfer.getAmount());

        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);

        transfer.complete();

        return transferMapper.toResponse(transfer);
    }

    @Transactional
    public TransferResponse failTransfer(String reference) {

        Transfer transfer = transferRepository.findByReferenceForUpdate(reference)
                .orElseThrow(() -> new TransferNotFoundException(reference));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new InvalidTransferStatusException("Transfer status must be PENDING");
        }

        Long sourceAccountId = transfer.getSourceAccount().getId();

        Account sourceAccount = accountRepository.findByIdForUpdate(sourceAccountId)
                .orElseThrow(AccountNotFoundException::new);

        sourceAccount.releaseReservation(transfer.getAmount());

        transfer.fail();

        return transferMapper.toResponse(transfer);
    }

    @Transactional(readOnly = true)
    public TransferResponse getTransferByReference(String reference) {

        Transfer transfer =
                transferRepository.findByReference(reference)
                        .orElseThrow(() ->
                                new TransferNotFoundException(reference)
                        );

        return transferMapper.toResponse(transfer);
    }

    @Transactional(readOnly = true)
    public Transfer getTransfer(String reference) {

        return transferRepository.findByReference(reference)
                .orElseThrow(() ->
                        new TransferNotFoundException(reference)
                );
    }
}
