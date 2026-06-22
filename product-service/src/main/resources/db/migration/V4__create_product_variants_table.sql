CREATE TABLE schema_product.product_variants (
                                                 id               BIGSERIAL       PRIMARY KEY,
                                                 product_id       BIGINT          NOT NULL REFERENCES schema_product.products(id) ON DELETE CASCADE,
                                                 sku              VARCHAR(100)    NOT NULL UNIQUE,
                                                 size             VARCHAR(20),
                                                 color            VARCHAR(50),
                                                 additional_price NUMERIC(10,2)   NOT NULL DEFAULT 0.00,
                                                 active           BOOLEAN         NOT NULL DEFAULT TRUE,
                                                 created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                                 updated_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                                 created_by       VARCHAR(100),
                                                 updated_by       VARCHAR(100)
);

CREATE INDEX idx_variants_product ON schema_product.product_variants (product_id);
CREATE INDEX idx_variants_sku     ON schema_product.product_variants (sku);

COMMENT ON COLUMN schema_product.product_variants.sku              IS 'Unique Stock Keeping Unit — referenced by Inventory Service';
COMMENT ON COLUMN schema_product.product_variants.additional_price IS 'Added to base product price for this variant';