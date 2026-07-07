CREATE TABLE schema_order.order_items (
                                          id                  BIGSERIAL       PRIMARY KEY,
                                          order_id            BIGINT          NOT NULL REFERENCES schema_order.orders(id) ON DELETE CASCADE,
                                          product_id          BIGINT          NOT NULL,
                                          product_variant_id  BIGINT,
                                          sku                 VARCHAR(100)    NOT NULL,
                                          product_name        VARCHAR(200)    NOT NULL,
                                          unit_price          NUMERIC(10,2)   NOT NULL,
                                          discount_price      NUMERIC(10,2)   NOT NULL DEFAULT 0.00,
                                          quantity            INTEGER         NOT NULL,
                                          subtotal            NUMERIC(12,2)   NOT NULL,
                                          created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                          updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                          created_by          VARCHAR(100),
                                          updated_by          VARCHAR(100)
);

CREATE INDEX idx_order_items_order ON schema_order.order_items (order_id);

COMMENT ON COLUMN schema_order.order_items.unit_price IS
    'Price snapshot at order time — NOT updated when product price changes';
COMMENT ON COLUMN schema_order.order_items.sku IS
    'References Inventory Service — used for stock reservation';