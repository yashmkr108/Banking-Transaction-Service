package com.yash.banking_transaction_service.repository;

import com.yash.banking_transaction_service.entity.OutboxEvent;
import com.yash.banking_transaction_service.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(value = """
    SELECT *
    FROM outbox_event
    WHERE status = :status
    ORDER BY created_at
    LIMIT 1
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    Optional<OutboxEvent> findNextPendingForUpdate(
            @Param("status") String status
    );

    @Modifying
    @Query("""
    UPDATE OutboxEvent e
    SET e.status = CASE
        WHEN e.attempts >= :maxAttempts THEN :failed
        ELSE :pending
    END,
    e.lockedBy = NULL,
    e.lockedAt = NULL
    WHERE e.status = :processing
      AND e.lockedAt < :cutoff
    """)
    int recoverStaleEvents(
            @Param("processing") OutboxStatus processing,
            @Param("pending") OutboxStatus pending,
            @Param("failed") OutboxStatus failed,
            @Param("maxAttempts") int maxAttempts,
            @Param("cutoff") Instant cutoff
    );
}
