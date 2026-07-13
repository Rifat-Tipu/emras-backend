CREATE TABLE schema_payment.payments (
                                         id                BIGSERIAL       PRIMARY KEY,
                                         order_id          BIGINT          NOT NULL,
                                         user_id           BIGINT          NOT NULL,
                                         amount            NUMERIC(12,2)   NOT NULL,
                                         status            VARCHAR(15)     NOT NULL DEFAULT 'PENDING',
                                         method            VARCHAR(15)     NOT NULL,
                                         transaction_id    VARCHAR(100),
                                         gateway_response  TEXT,
                                         failure_reason    VARCHAR(500),
                                         paid_at           TIMESTAMPTZ,
                                         refunded_at       TIMESTAMPTZ,
                                         refund_amount     NUMERIC(12,2),
                                         created_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                         updated_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_order_id       ON schema_payment.payments (order_id);
CREATE INDEX idx_payment_status         ON schema_payment.payments (status);
CREATE INDEX idx_payment_transaction_id ON schema_payment.payments (transaction_id);

COMMENT ON COLUMN schema_payment.payments.status IS
    'PENDING | SUCCESS | FAILED | REFUNDED | PARTIAL_REFUND';
COMMENT ON COLUMN schema_payment.payments.gateway_response IS
    'Raw JSON response from payment gateway — stored for audit';