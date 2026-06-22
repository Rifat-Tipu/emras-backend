CREATE TABLE schema_product.products (
                                         id               BIGSERIAL       PRIMARY KEY,
                                         name_en          VARCHAR(200)    NOT NULL,
                                         name_bn          VARCHAR(200)    NOT NULL,
                                         description_en   TEXT,
                                         description_bn   TEXT,
                                         slug             VARCHAR(250)    NOT NULL UNIQUE,
                                         price            NUMERIC(10,2)   NOT NULL,
                                         discount_price   NUMERIC(10,2),
                                         status           VARCHAR(10)     NOT NULL DEFAULT 'DRAFT',
                                         featured         BOOLEAN         NOT NULL DEFAULT FALSE,
                                         category_id      BIGINT          REFERENCES schema_product.categories(id),
                                         created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                         updated_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                         created_by       VARCHAR(100),
                                         updated_by       VARCHAR(100)
);

CREATE INDEX idx_products_slug     ON schema_product.products (slug);
CREATE INDEX idx_products_status   ON schema_product.products (status);
CREATE INDEX idx_products_category ON schema_product.products (category_id);
CREATE INDEX idx_products_featured ON schema_product.products (featured);

COMMENT ON COLUMN schema_product.products.price          IS 'Base price in BDT (Bangladeshi Taka)';
COMMENT ON COLUMN schema_product.products.discount_price IS 'Discounted price — NULL means no active discount';
COMMENT ON COLUMN schema_product.products.status         IS 'DRAFT | ACTIVE | ARCHIVED';