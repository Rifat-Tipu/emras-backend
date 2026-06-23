CREATE TABLE schema_inventory.inventory_audit_log (
                                                      id               BIGSERIAL       PRIMARY KEY,
                                                      sku              VARCHAR(100)    NOT NULL,
                                                      order_id         BIGINT,
                                                      movement_type    VARCHAR(20)     NOT NULL,
                                                      quantity_change  INTEGER         NOT NULL,
                                                      quantity_before  INTEGER         NOT NULL,
                                                      quantity_after   INTEGER         NOT NULL,
                                                      notes            VARCHAR(255),
                                                      created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                                      created_by       VARCHAR(100)
);

CREATE INDEX idx_audit_sku       ON schema_inventory.inventory_audit_log (sku);
CREATE INDEX idx_audit_order_id  ON schema_inventory.inventory_audit_log (order_id);
CREATE INDEX idx_audit_created   ON schema_inventory.inventory_audit_log (created_at);

COMMENT ON TABLE schema_inventory.inventory_audit_log IS 'Append-only log of every stock movement. Never updated or deleted.';
COMMENT ON COLUMN schema_inventory.inventory_audit_log.movement_type IS 'STOCK_IN | STOCK_ADJUST | RESERVED | RELEASED | DEDUCTED';