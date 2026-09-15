package com.yash.banking_transaction_service.entity;

import com.yash.banking_transaction_service.enums.AccountStatus;
import com.yash.banking_transaction_service.exceptions.AccountStateException;
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

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "reserved_balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal reservedBalance;

    @Version
    @Column(nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Account(String ownerName, String accountNumber) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = BigDecimal.ZERO;
        this.reservedBalance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void block() {

        if (status == AccountStatus.CLOSED) {
            throw new AccountStateException("Closed account cannot be blocked ");
        }

        if (status == AccountStatus.BLOCKED) {
            throw new AccountStateException("Account is already blocked");
        }

        status = AccountStatus.BLOCKED;
        updatedAt = Instant.now();
    }

    public void activate() {

        if (status == AccountStatus.CLOSED) {
            throw new AccountStateException("Closed account cannot be activated");
        }

        if (status == AccountStatus.ACTIVE) {
            throw new AccountStateException("Account is already active");
        }

        status = AccountStatus.ACTIVE;
        updatedAt = Instant.now();
    }

    public void close() {

        if (status == AccountStatus.CLOSED) {
            throw new AccountStateException("Account is already closed");
        }

        status = AccountStatus.CLOSED;
        updatedAt = Instant.now();
    }

}
