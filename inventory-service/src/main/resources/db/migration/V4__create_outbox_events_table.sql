CREATE TABLE schema_inventory.outbox_events (
                                                id              BIGSERIAL       PRIMARY KEY,
                                                event_id        VARCHAR(36)     NOT NULL UNIQUE,
                                                topic           VARCHAR(100)    NOT NULL,
                                                message_key     VARCHAR(100),
                                                payload         TEXT            NOT NULL,
                                                status          VARCHAR(10)     NOT NULL DEFAULT 'PENDING',
                                                retry_count     INTEGER         NOT NULL DEFAULT 0,
                                                last_attempt_at TIMESTAMPTZ,
                                                failed_at       TIMESTAMPTZ,
                                                error_message   VARCHAR(500),
                                                created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                                published_at    TIMESTAMPTZ
);

CREATE INDEX idx_outbox_status  ON schema_inventory.outbox_events (status);
CREATE INDEX idx_outbox_created ON schema_inventory.outbox_events (created_at);
CREATE INDEX idx_outbox_event_id ON schema_inventory.outbox_events (event_id);

COMMENT ON TABLE schema_inventory.outbox_events IS
    'Outbox pattern — events written here atomically with DB changes, published to Kafka by scheduler';
COMMENT ON COLUMN schema_inventory.outbox_events.status IS 'PENDING | PUBLISHED | FAILED';