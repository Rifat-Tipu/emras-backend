-- =============================================================================
--  V5__create_refresh_tokens_table.sql
--  Persisted refresh token records for audit trail and multi-device management.
--  Fast validation uses Redis; DB is the source of truth for revocation.
-- =============================================================================

CREATE TABLE schema_auth.refresh_tokens (
                                            id          BIGSERIAL       PRIMARY KEY,
                                            jti         VARCHAR(100)    NOT NULL UNIQUE,    -- JWT ID — used as Redis key
                                            user_id     BIGINT          NOT NULL REFERENCES schema_auth.users (id) ON DELETE CASCADE,
                                            expires_at  TIMESTAMPTZ     NOT NULL,
                                            revoked     BOOLEAN         NOT NULL DEFAULT FALSE,
                                            user_agent  VARCHAR(255),
                                            ip_address  VARCHAR(50),

    -- Audit columns
                                            created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                            updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                            created_by  VARCHAR(100),
                                            updated_by  VARCHAR(100)
);

CREATE INDEX idx_refresh_token_jti  ON schema_auth.refresh_tokens (jti);
CREATE INDEX idx_refresh_token_user ON schema_auth.refresh_tokens (user_id);

COMMENT ON TABLE  schema_auth.refresh_tokens         IS 'Persisted refresh token records. Redis is the primary validation store.';
COMMENT ON COLUMN schema_auth.refresh_tokens.jti     IS 'JWT ID — unique identifier. Stored as cookie value on client.';
COMMENT ON COLUMN schema_auth.refresh_tokens.revoked IS 'TRUE if token was explicitly revoked (logout / token rotation)';