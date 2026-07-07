CREATE TABLE schema_order.outbox_events (
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

CREATE INDEX idx_order_outbox_status   ON schema_order.outbox_events (status);
CREATE INDEX idx_order_outbox_event_id ON schema_order.outbox_events (event_id);