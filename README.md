# Banking Transaction Service

A production-oriented Spring Boot backend focused on **transactional correctness, concurrency, idempotency, failure recovery, and reliable asynchronous processing**.

> Learning/portfolio project. Not intended for real financial transactions.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Docker
- Spring Retry
- MapStruct

## Architecture

```text
Client
  ↓
Transfer API
  ↓
Idempotency
  ↓
Transfer Orchestrator
  ↓
┌─────────────────────────────┐
│ Transaction                 │
│                             │
│ Account Locking             │
│ Balance Update              │
│ Transfer                    │
│ Ledger Entry                │
│ Audit Log                   │
│ Outbox Event                │
└─────────────────────────────┘
  ↓
Commit
  ↓
Outbox Worker
  ↓
External Email
````

## Key Features

### Transaction Management

* Atomic transfer execution using `@Transactional`
* PostgreSQL constraints for financial invariants
* Flyway-managed schema with `ddl-auto=validate`

### Concurrency Control

* Pessimistic locking with `PESSIMISTIC_WRITE`
* Deterministic lock ordering to reduce deadlocks
* Tested with concurrent requests and high contention

### Idempotency

* Idempotency keys
* Request fingerprinting
* Database-level unique constraint
* Safe handling of duplicate requests

### Failure & Retry

* Transaction rollback on failures
* Retry of transient failures using fresh transactions
* Deadlock/retry handling
* Safe recovery of pending transfers

### Transactional Outbox

* Business state and outbox event committed atomically
* Multiple workers using PostgreSQL `SKIP LOCKED`
* Stale-event recovery
* At-least-once delivery semantics

### Audit & Ledger

* Immutable ledger entries for completed transfers
* Transactional audit logging
* Transfer state management:
  `PENDING → COMPLETED / FAILED`

## 🧪 What Was Tested

* Concurrent transfers
* Concurrent idempotency requests
* Failure + retry recovery
* Duplicate transfer execution
* Deadlocks and retry
* 200 concurrent requests against the same account
* Multiple outbox workers
* Worker failure and stale-event recovery
* Duplicate external delivery

## Core Domain

```text
Account
Transfer
LedgerEntry
IdempotencyRecord
AuditLog
OutboxEvent
```

## Running Locally

### Start PostgreSQL

```bash
docker compose up -d
```

### Configure environment variables

```env
spring.datasource.url=jdbc:postgresql://localhost:5432/banking
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=validate

spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

outbox.retry.max-attempts: ${OUTBOX_RETRY_MAX_ATTEMPTS:3}
spring.jpa.open-in-view=false
```

### Run

```bash
./mvnw spring-boot:run
```

Flyway migrations run automatically on startup.

## 💡 What This Project Demonstrates

The project focuses on a simple question:

> **How do you keep financial state correct when requests are concurrent, retried, partially fail, or trigger external side effects?**

The main design principles are:

**Transactions + Locking + Idempotency + Retry + Outbox + Database Constraints**
