CREATE TABLE schema_order.order_status_history (
                                                   id         BIGSERIAL       PRIMARY KEY,
                                                   order_id   BIGINT          NOT NULL REFERENCES schema_order.orders(id) ON DELETE CASCADE,
                                                   status     VARCHAR(15)     NOT NULL,
                                                   note       VARCHAR(255),
                                                   created_at TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_history_order ON schema_order.order_status_history (order_id);

COMMENT ON TABLE schema_order.order_status_history IS
    'Append-only log of every status change — never updated or deleted';