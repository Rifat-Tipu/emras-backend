CREATE TABLE schema_product.categories (
                                           id          BIGSERIAL       PRIMARY KEY,
                                           name_en     VARCHAR(100)    NOT NULL,
                                           name_bn     VARCHAR(100)    NOT NULL,
                                           slug        VARCHAR(150)    NOT NULL UNIQUE,
                                           parent_id   BIGINT          REFERENCES schema_product.categories(id),
                                           image_url   VARCHAR(255),
                                           active      BOOLEAN         NOT NULL DEFAULT TRUE,
                                           created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                           updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                           created_by  VARCHAR(100),
                                           updated_by  VARCHAR(100)
);

CREATE INDEX idx_categories_parent ON schema_product.categories (parent_id);
CREATE INDEX idx_categories_slug   ON schema_product.categories (slug);