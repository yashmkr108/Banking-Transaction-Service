package com.yash.banking_transaction_service.repository;

import com.yash.banking_transaction_service.entity.OutboxEvent;
import com.yash.banking_transaction_service.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    Optional<OutboxEvent> findFirstByStatusOrderByCreatedAtAsc(
            OutboxStatus status
    );

}
