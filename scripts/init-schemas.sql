-- =============================================================================
--  scripts/init-schemas.sql
--  Runs ONCE when the PostgreSQL Docker container is first created.
--  Pre-creates all service schemas so Flyway finds them on first boot.
--
--  Location in docker-compose.yml:
--    volumes:
--      - ./scripts/init-schemas.sql:/docker-entrypoint-initdb.d/init-schemas.sql
-- =============================================================================

-- Auth Service
CREATE SCHEMA IF NOT EXISTS schema_auth;

-- User Service
CREATE SCHEMA IF NOT EXISTS schema_user;

-- Product Service
CREATE SCHEMA IF NOT EXISTS schema_product;

-- Inventory Service
CREATE SCHEMA IF NOT EXISTS schema_inventory;

-- Order Service
CREATE SCHEMA IF NOT EXISTS schema_order;

-- Payment Service
CREATE SCHEMA IF NOT EXISTS schema_payment;

-- Notification Service
CREATE SCHEMA IF NOT EXISTS schema_notification;

-- Delivery Service
CREATE SCHEMA IF NOT EXISTS schema_delivery;

-- AI Assistant Service
CREATE SCHEMA IF NOT EXISTS schema_ai;

-- Review Service
CREATE SCHEMA IF NOT EXISTS schema_review;

-- Analytics Service
CREATE SCHEMA IF NOT EXISTS schema_analytics;

-- Discount Service
CREATE SCHEMA IF NOT EXISTS schema_discount;

-- Grant all permissions to the app user on every schema
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_auth         TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_user         TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_product      TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_inventory    TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_order        TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_payment      TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_notification TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_delivery     TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_ai           TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_review       TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_analytics    TO emras_user;
GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA schema_discount     TO emras_user;

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_auth         TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_user         TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_product      TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_inventory    TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_order        TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_payment      TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_notification TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_delivery     TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_ai           TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_review       TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_analytics    TO emras_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA schema_discount     TO emras_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA schema_auth         GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_user         GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_product      GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_inventory    GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_order        GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_payment      GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_notification GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_delivery     GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_ai           GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_review       GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_analytics    GRANT ALL ON TABLES    TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_discount     GRANT ALL ON TABLES    TO emras_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA schema_auth         GRANT ALL ON SEQUENCES TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_order        GRANT ALL ON SEQUENCES TO emras_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_product      GRANT ALL ON SEQUENCES TO emras_user;
