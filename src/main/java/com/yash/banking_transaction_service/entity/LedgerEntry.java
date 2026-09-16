package com.yash.banking_transaction_service.entity;

import com.yash.banking_transaction_service.enums.LedgerEntryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ledger_entry")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LedgerEntryType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public LedgerEntry(
            Transfer transfer,
            Account account,
            LedgerEntryType type,
            BigDecimal amount
    ) {
        this.transfer = transfer;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

}
