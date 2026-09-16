package com.yash.banking_transaction_service.repository;

import com.yash.banking_transaction_service.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry,Long> {
}
