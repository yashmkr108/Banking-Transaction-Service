package com.yash.banking_transaction_service.entity;

import com.yash.banking_transaction_service.enums.AuditAction;
import com.yash.banking_transaction_service.enums.AuditStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
@Getter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(length = 100)
    private String reference;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @Column(length = 500)
    private String message;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    public AuditLog(AuditAction action, String reference, AuditStatus status, String message) {
        this.action = action;
        this.reference = reference;
        this.status = status;
        this.message = message;
        this.createdAt = Instant.now();
    }

}
