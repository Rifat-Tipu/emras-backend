CREATE TABLE schema_user.user_profiles (
                                           id                  BIGSERIAL       PRIMARY KEY,
                                           user_id             BIGINT          NOT NULL UNIQUE,
                                           first_name          VARCHAR(100),
                                           last_name           VARCHAR(100),
                                           phone               VARCHAR(20),
                                           avatar_url          VARCHAR(255),
                                           preferred_language  VARCHAR(5)      NOT NULL DEFAULT 'EN',
                                           gender              VARCHAR(10),
                                           created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                           updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                           created_by          VARCHAR(100),
                                           updated_by          VARCHAR(100)
);

CREATE INDEX idx_user_profiles_user_id ON schema_user.user_profiles (user_id);

COMMENT ON TABLE schema_user.user_profiles IS 'Customer profile data — user_id matches Auth Service users.id';