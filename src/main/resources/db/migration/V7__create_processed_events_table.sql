-- V7: Create processed events table to assure idempotency

CREATE TABLE processed_events (
    event_id VARCHAR(36) PRIMARY KEY,
    processed_at DATETIME NOT NULL
);