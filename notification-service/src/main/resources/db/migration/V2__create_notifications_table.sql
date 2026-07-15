CREATE TABLE schema_notification.notifications (
                                                   id             BIGSERIAL       PRIMARY KEY,
                                                   user_id        BIGINT,
                                                   type           VARCHAR(30)     NOT NULL,
                                                   channel        VARCHAR(10)     NOT NULL,
                                                   title          VARCHAR(200)    NOT NULL,
                                                   body           TEXT            NOT NULL,
                                                   reference_id   BIGINT,
                                                   reference_type VARCHAR(50),
                                                   is_read        BOOLEAN         NOT NULL DEFAULT FALSE,
                                                   is_sent        BOOLEAN         NOT NULL DEFAULT FALSE,
                                                   failure_reason VARCHAR(500),
                                                   created_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                                                   updated_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_user_id ON schema_notification.notifications (user_id);
CREATE INDEX idx_notif_type    ON schema_notification.notifications (type);
CREATE INDEX idx_notif_read    ON schema_notification.notifications (is_read);
CREATE INDEX idx_notif_created ON schema_notification.notifications (created_at);

COMMENT ON COLUMN schema_notification.notifications.user_id IS
    'NULL for admin-only notifications (low stock, out of stock alerts)';
COMMENT ON COLUMN schema_notification.notifications.channel IS
    'EMAIL | SMS | IN_APP';