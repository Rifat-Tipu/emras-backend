CREATE TABLE schema_user.wishlist_items (
                                            id          BIGSERIAL   PRIMARY KEY,
                                            user_id     BIGINT      NOT NULL,
                                            product_id  BIGINT      NOT NULL,
                                            created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                            updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                            created_by  VARCHAR(100),
                                            updated_by  VARCHAR(100),

                                            CONSTRAINT uk_wishlist_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX idx_wishlist_user_id ON schema_user.wishlist_items (user_id);

COMMENT ON TABLE schema_user.wishlist_items IS 'Product wishlist — product_id references Product Service';