package com.yash.banking_transaction_service.entity;

import com.yash.banking_transaction_service.enums.OutboxEventType;
import com.yash.banking_transaction_service.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    private OutboxEventType eventType;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "locked_by", length = 100)
    private String lockedBy;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    public OutboxEvent(
            OutboxEventType eventType,
            String aggregateType,
            String aggregateId,
            String payload
    ) {
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.attempts = 0;
        this.createdAt = Instant.now();
    }

    public void markProcessing(String workerId) {
        if (status != OutboxStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending events can be marked as processing"
            );
        }

        status = OutboxStatus.PROCESSING;
        attempts++;
        lockedBy = workerId;
        lockedAt = Instant.now();
    }

    public void markCompleted() {
        if (status != OutboxStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing events can be completed"
            );
        }

        status = OutboxStatus.COMPLETED;
        processedAt = Instant.now();
        lockedBy = null;
        lockedAt = null;
    }

    public void markFailed() {
        if (status != OutboxStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing events can be failed"
            );
        }

        status = OutboxStatus.FAILED;
        lockedBy = null;
        lockedAt = null;
    }
}
