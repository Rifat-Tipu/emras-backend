CREATE TABLE schema_inventory.processed_events (
                                                   id             BIGSERIAL       PRIMARY KEY,
                                                   event_id       VARCHAR(36)     NOT NULL,
                                                   consumer_group VARCHAR(100)    NOT NULL,
                                                   processed_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

                                                   CONSTRAINT uk_processed_event UNIQUE (event_id, consumer_group)
);

CREATE INDEX idx_processed_event_id ON schema_inventory.processed_events (event_id);

COMMENT ON TABLE schema_inventory.processed_events IS
    'Idempotency store — tracks consumed Kafka event IDs to prevent duplicate processing';