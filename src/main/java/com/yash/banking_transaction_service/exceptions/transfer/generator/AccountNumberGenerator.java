package com.yash.banking_transaction_service.exceptions.transfer.generator;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {

    private final EntityManager entityManager;

    public AccountNumberGenerator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String generate() {
        Number sequenceValue = (Number) entityManager
                .createNativeQuery("SELECT nextval('account_number_seq')")
                .getSingleResult();

        return sequenceValue.toString();
    }
}
