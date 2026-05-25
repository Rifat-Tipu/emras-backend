-- =============================================================================
--  V1__create_schema_auth.sql
--  Creates the dedicated schema for the Auth Service.
--  Every Auth Service table lives inside schema_auth — no other service
--  can query this schema directly.
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS schema_auth;