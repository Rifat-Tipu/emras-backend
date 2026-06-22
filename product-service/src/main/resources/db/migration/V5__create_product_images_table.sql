CREATE TABLE schema_product.product_images (
                                               id            BIGSERIAL       PRIMARY KEY,
                                               product_id    BIGINT          NOT NULL REFERENCES schema_product.products(id) ON DELETE CASCADE,
                                               url           VARCHAR(500)    NOT NULL,
                                               alt_text      VARCHAR(200),
                                               is_primary    BOOLEAN         NOT NULL DEFAULT FALSE,
                                               display_order INTEGER         NOT NULL DEFAULT 0,
                                               created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                               updated_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                               created_by    VARCHAR(100),
                                               updated_by    VARCHAR(100)
);

CREATE INDEX idx_images_product ON schema_product.product_images (product_id);