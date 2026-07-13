package com.emras.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_events", schema = "schema_payment",
        indexes = {
                @Index(name = "idx_payment_outbox_status",   columnList = "status"),
                @Index(name = "idx_payment_outbox_event_id", columnList = "event_id", unique = true)
        })
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(nullable = false, length = 100)
    private String topic;

    @Column(name = "message_key", length = 100)
    private String messageKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private int retryCount = 0;

    private Instant lastAttemptAt;
    private Instant failedAt;

    @Column(length = 500)
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant publishedAt;

    public enum OutboxStatus { PENDING, PUBLISHED, FAILED }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}