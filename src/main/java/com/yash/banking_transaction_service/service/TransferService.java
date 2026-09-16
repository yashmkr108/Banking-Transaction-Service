package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.dto.CreateTransferRequest;
import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.entity.Account;
import com.yash.banking_transaction_service.entity.Transfer;
import com.yash.banking_transaction_service.enums.AccountStatus;
import com.yash.banking_transaction_service.exceptions.AccountNotFoundException;
import com.yash.banking_transaction_service.exceptions.InactiveAccountException;
import com.yash.banking_transaction_service.exceptions.SameAccountTransferException;
import com.yash.banking_transaction_service.generator.TransferReferenceGenerator;
import com.yash.banking_transaction_service.mapper.TransferMapper;
import com.yash.banking_transaction_service.repository.AccountRepository;
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

    public TransferService(
            AccountRepository accountRepository,
            TransferRepository transferRepository,
            TransferReferenceGenerator transferReferenceGenerator,
            TransferMapper transferMapper
    ) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.transferReferenceGenerator = transferReferenceGenerator;
        this.transferMapper = transferMapper;
    }

    @Transactional
    public TransferResponse createTransfer(CreateTransferRequest request) {

        String sourceAccountNumber = request.sourceAccountNumber();
        String destinationAccountNumber = request.destinationAccountNumber();
        BigDecimal amount = request.amount();

        if(sourceAccountNumber.equals(destinationAccountNumber)){
            throw new SameAccountTransferException();
        }

        Account sourceAccount = accountRepository.findByAccountNumberForUpdate(sourceAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(sourceAccountNumber));
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(destinationAccountNumber));



        if(sourceAccount.getStatus() != AccountStatus.ACTIVE
                || destinationAccount.getStatus() != AccountStatus.ACTIVE){
            throw new InactiveAccountException();
        }

        sourceAccount.reserve(amount);

        String reference = transferReferenceGenerator.generate();

        Transfer transfer = new Transfer(reference,sourceAccount,destinationAccount,amount);

        Transfer savedTransfer = transferRepository.save(transfer);

        return transferMapper.toResponse(savedTransfer);
    }

}
