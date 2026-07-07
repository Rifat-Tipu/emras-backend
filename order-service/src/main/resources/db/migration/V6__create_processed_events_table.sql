CREATE TABLE schema_order.processed_events (
                                               id             BIGSERIAL       PRIMARY KEY,
                                               event_id       VARCHAR(36)     NOT NULL,
                                               consumer_group VARCHAR(100)    NOT NULL,
                                               processed_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

                                               CONSTRAINT uk_order_processed_event UNIQUE (event_id, consumer_group)
);

CREATE INDEX idx_order_processed_event ON schema_order.processed_events (event_id);