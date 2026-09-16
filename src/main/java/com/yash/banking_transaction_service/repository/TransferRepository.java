package com.yash.banking_transaction_service.repository;

import com.yash.banking_transaction_service.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer,Long> {
}
