CREATE TABLE schema_user.addresses (
                                       id           BIGSERIAL       PRIMARY KEY,
                                       user_id      BIGINT          NOT NULL,
                                       label        VARCHAR(10)     NOT NULL DEFAULT 'HOME',
                                       division     VARCHAR(50)     NOT NULL,
                                       district     VARCHAR(50)     NOT NULL,
                                       thana        VARCHAR(100)    NOT NULL,
                                       area         VARCHAR(255),
                                       house_number VARCHAR(255)    NOT NULL,
                                       postal_code  VARCHAR(10),
                                       is_default   BOOLEAN         NOT NULL DEFAULT FALSE,
                                       created_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                       updated_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                       created_by   VARCHAR(100),
                                       updated_by   VARCHAR(100)
);

CREATE INDEX idx_addresses_user_id ON schema_user.addresses (user_id);

COMMENT ON TABLE schema_user.addresses IS 'Delivery addresses with Bangladesh-specific fields';
COMMENT ON COLUMN schema_user.addresses.thana IS 'Upazila/Thana — administrative sub-district';