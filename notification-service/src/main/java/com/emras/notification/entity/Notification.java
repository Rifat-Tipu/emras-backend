package com.emras.notification.entity;
import jakarta.persistence.*;
import lombok.*;
/**
 * Stores every notification sent to a user.
 *
 * Used for:
 *  - In-app notification history
 *  - Audit trail of all emails/SMS sent
 *  - Read/unread tracking per user
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications", schema = "schema_notification",
        indexes = {
                @Index(name = "idx_notif_user_id",  columnList = "user_id"),
                @Index(name = "idx_notif_type",     columnList = "type"),
                @Index(name = "idx_notif_read",     columnList = "is_read"),
                @Index(name = "idx_notif_created",  columnList = "created_at")
        })
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User this notification belongs to — null for admin-only alerts */
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NotificationChannel channel;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /** Reference to the triggering entity e.g. orderId, paymentId */
    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    /** Whether delivery succeeded */
    @Column(name = "is_sent", nullable = false)
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    public enum NotificationType {
        WELCOME,
        ORDER_CONFIRMED,
        ORDER_CANCELLED,
        PAYMENT_SUCCESS,
        PAYMENT_FAILED,
        PAYMENT_REFUNDED,
        LOW_STOCK_ALERT,
        OUT_OF_STOCK_ALERT
    }

    public enum NotificationChannel {
        EMAIL, SMS, IN_APP
    }
}