package com.yash.banking_transaction_service.entity;

import com.yash.banking_transaction_service.enums.AccountStatus;
import com.yash.banking_transaction_service.exceptions.account.InsufficientAvailableBalanceException;
import com.yash.banking_transaction_service.exceptions.account.InsufficientReservedBalanceException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_name", nullable = false, length = 150)
    private String ownerName;

    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "reserved_balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal reservedBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Account(String ownerName, String accountNumber ,String email) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.email = email;
        this.balance = BigDecimal.ZERO;
        this.reservedBalance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void block() {
        status = AccountStatus.BLOCKED;
        updatedAt = Instant.now();
    }

    public void activate() {
        status = AccountStatus.ACTIVE;
        updatedAt = Instant.now();
    }

    public void close() {
        status = AccountStatus.CLOSED;
        updatedAt = Instant.now();
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
        updatedAt = Instant.now();
    }

    public void reserve(BigDecimal amount) {
        if (reservedBalance.add(amount).compareTo(balance) > 0) {
            throw new InsufficientAvailableBalanceException();
        }
        reservedBalance = reservedBalance.add(amount);
        updatedAt = Instant.now();
    }

    public void settleDebit(BigDecimal amount) {
        if (reservedBalance.compareTo(amount) < 0) {
            throw new InsufficientReservedBalanceException();
        }
        reservedBalance = reservedBalance.subtract(amount);
        balance = balance.subtract(amount);
        updatedAt = Instant.now();
    }

    public void settleCredit(BigDecimal amount) {
        balance = balance.add(amount);
        updatedAt = Instant.now();
    }

    public void releaseReservation(BigDecimal amount) {
        if (reservedBalance.compareTo(amount) < 0) {
            throw new InsufficientReservedBalanceException();
        }

        reservedBalance = reservedBalance.subtract(amount);
        updatedAt = Instant.now();
    }
}
