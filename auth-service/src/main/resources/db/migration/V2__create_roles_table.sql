-- =============================================================================
--  V2__create_roles_table.sql
-- =============================================================================

CREATE TABLE schema_auth.roles (
                                   id          BIGSERIAL       PRIMARY KEY,
                                   name        VARCHAR(20)     NOT NULL UNIQUE,

    -- Audit columns (populated by JPA @CreatedDate / @LastModifiedDate)
                                   created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                   updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                   created_by  VARCHAR(100),
                                   updated_by  VARCHAR(100)
);

COMMENT ON TABLE  schema_auth.roles      IS 'Application roles: ROLE_CUSTOMER, ROLE_ADMIN, ROLE_STAFF, ROLE_VIEWER';
COMMENT ON COLUMN schema_auth.roles.name IS 'Role name — must match Role.RoleName enum exactly';