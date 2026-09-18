ALTER TABLE ledger_entry
    ADD CONSTRAINT uq_ledger_transfer_type
        UNIQUE (transfer_id, type);