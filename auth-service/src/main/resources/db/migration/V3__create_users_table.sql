-- =============================================================================
--  V3__create_users_table.sql
-- =============================================================================

CREATE TABLE schema_auth.users (
                                   id              BIGSERIAL       PRIMARY KEY,
                                   email           VARCHAR(150)    NOT NULL UNIQUE,
                                   password        VARCHAR(255),                       -- NULL for OAuth2-only accounts
                                   phone           VARCHAR(20),
                                   auth_provider   VARCHAR(20)     NOT NULL DEFAULT 'LOCAL',
                                   provider_id     VARCHAR(255),                       -- Google/Facebook user ID
                                   email_verified  BOOLEAN         NOT NULL DEFAULT FALSE,
                                   enabled         BOOLEAN         NOT NULL DEFAULT TRUE,
                                   account_locked  BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Audit columns
                                   created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                   updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                   created_by      VARCHAR(100),
                                   updated_by      VARCHAR(100)
);

-- Index on email is the most frequent lookup (login)
CREATE INDEX idx_users_email    ON schema_auth.users (email);
-- Index on phone for OTP login lookup
CREATE INDEX idx_users_phone    ON schema_auth.users (phone);
-- Index for OAuth2 provider lookups
CREATE INDEX idx_users_provider ON schema_auth.users (provider_id, auth_provider);

COMMENT ON TABLE  schema_auth.users               IS 'Core user accounts for the Emras platform';
COMMENT ON COLUMN schema_auth.users.password      IS 'BCrypt-encoded password. NULL for OAuth2-only accounts';
COMMENT ON COLUMN schema_auth.users.auth_provider IS 'LOCAL | GOOGLE | FACEBOOK';
COMMENT ON COLUMN schema_auth.users.provider_id   IS 'External OAuth2 user ID from Google or Facebook';