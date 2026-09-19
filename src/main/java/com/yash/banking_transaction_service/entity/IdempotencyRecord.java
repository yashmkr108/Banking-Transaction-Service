package com.yash.banking_transaction_service.entity;

import com.yash.banking_transaction_service.enums.IdempotencyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "idempotency_record")
@Getter
@NoArgsConstructor
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "request_fingerprint", nullable = false, length = 64)
    private String requestFingerprint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IdempotencyStatus status;

    @Column(name = "transfer_reference", length = 100)
    private String transferReference;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public IdempotencyRecord(
            String idempotencyKey,
            String requestFingerprint
    ) {
        this.idempotencyKey = idempotencyKey;
        this.requestFingerprint = requestFingerprint;
        this.status = IdempotencyStatus.PROCESSING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void attachTransfer(String transferReference){
        this.transferReference = transferReference;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        if (status != IdempotencyStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing idempotency records can be completed"
            );
        }

        status = IdempotencyStatus.COMPLETED;
        updatedAt = Instant.now();
    }

    public void fail() {
        if (status != IdempotencyStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing idempotency records can be failed"
            );
        }

        status = IdempotencyStatus.FAILED;
        updatedAt = Instant.now();
    }
}
