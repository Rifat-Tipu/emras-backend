-- =============================================================================
--  V6__insert_default_roles.sql
--  Seeds the four default roles. These must exist before any user registers.
--  ON CONFLICT DO NOTHING makes this script safe to re-run.
-- =============================================================================

INSERT INTO schema_auth.roles (name, created_by, updated_by)
VALUES
    ('ROLE_CUSTOMER', 'system', 'system'),
    ('ROLE_ADMIN',    'system', 'system'),
    ('ROLE_STAFF',    'system', 'system'),
    ('ROLE_VIEWER',   'system', 'system')
    ON CONFLICT (name) DO NOTHING;