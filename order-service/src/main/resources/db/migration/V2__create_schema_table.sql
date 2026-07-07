CREATE TABLE schema_order.orders (
                                     id               BIGSERIAL       PRIMARY KEY,
                                     user_id          BIGINT          NOT NULL,
                                     status           VARCHAR(15)     NOT NULL DEFAULT 'PENDING',
                                     total_amount     NUMERIC(12,2)   NOT NULL,
                                     discount_amount  NUMERIC(10,2)   NOT NULL DEFAULT 0.00,
                                     delivery_charge  NUMERIC(8,2)    NOT NULL DEFAULT 0.00,
                                     coupon_code      VARCHAR(50),
                                     payment_method   VARCHAR(20),
                                     delivery_address VARCHAR(500)    NOT NULL,
                                     address_id       BIGINT,
                                     notes            VARCHAR(500),
                                     created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                     updated_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                     created_by       VARCHAR(100),
                                     updated_by       VARCHAR(100)
);

CREATE INDEX idx_orders_user_id ON schema_order.orders (user_id);
CREATE INDEX idx_orders_status  ON schema_order.orders (status);
CREATE INDEX idx_orders_created ON schema_order.orders (created_at);

COMMENT ON COLUMN schema_order.orders.status IS
    'PENDING | CONFIRMED | PROCESSING | COMPLETED | CANCELLED | FAILED';
COMMENT ON COLUMN schema_order.orders.delivery_address IS
    'Snapshot of address at order time — preserved even if user changes address later';