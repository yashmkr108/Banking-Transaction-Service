CREATE INDEX idx_outbox_pending_created
    ON outbox_event (created_at)
    WHERE status = 'PENDING';

CREATE INDEX idx_outbox_processing_locked
    ON outbox_event (locked_at)
    WHERE status = 'PROCESSING';