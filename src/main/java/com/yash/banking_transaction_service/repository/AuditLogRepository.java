package com.yash.banking_transaction_service.repository;

import com.yash.banking_transaction_service.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
