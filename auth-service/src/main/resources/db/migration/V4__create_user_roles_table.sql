-- =============================================================================
--  V4__create_user_roles_table.sql
--  Join table for the many-to-many relationship between users and roles.
-- =============================================================================

CREATE TABLE schema_auth.user_roles (
                                        user_id     BIGINT  NOT NULL REFERENCES schema_auth.users (id) ON DELETE CASCADE,
                                        role_id     BIGINT  NOT NULL REFERENCES schema_auth.roles (id) ON DELETE CASCADE,
                                        PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON schema_auth.user_roles (user_id);
CREATE INDEX idx_user_roles_role ON schema_auth.user_roles (role_id);