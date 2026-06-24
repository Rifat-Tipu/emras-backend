CREATE TABLE schema_inventory.inventory_items (
                                                  id                  BIGSERIAL       PRIMARY KEY,
                                                  sku                 VARCHAR(100)    NOT NULL UNIQUE,
                                                  product_name        VARCHAR(200),
                                                  product_variant_id  BIGINT,
                                                  quantity            INTEGER         NOT NULL DEFAULT 0,
                                                  reserved_quantity   INTEGER         NOT NULL DEFAULT 0,
                                                  low_stock_threshold INTEGER         NOT NULL DEFAULT 5,
                                                  version             BIGINT          NOT NULL DEFAULT 0,
                                                  created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                                  updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                                  created_by          VARCHAR(100),
                                                  updated_by          VARCHAR(100),

                                                  CONSTRAINT chk_quantity_non_negative         CHECK (quantity >= 0),
                                                  CONSTRAINT chk_reserved_non_negative         CHECK (reserved_quantity >= 0),
                                                  CONSTRAINT chk_reserved_lte_quantity         CHECK (reserved_quantity <= quantity)
);

CREATE INDEX idx_inventory_sku            ON schema_inventory.inventory_items (sku);
CREATE INDEX idx_inventory_variant_id     ON schema_inventory.inventory_items (product_variant_id);

COMMENT ON COLUMN schema_inventory.inventory_items.version            IS 'Optimistic locking — auto-incremented by JPA on every update';
COMMENT ON COLUMN schema_inventory.inventory_items.reserved_quantity  IS 'Stock held for orders currently in checkout or payment';
COMMENT ON COLUMN schema_inventory.inventory_items.low_stock_threshold IS 'Kafka alert fires when availableQuantity drops to this value';