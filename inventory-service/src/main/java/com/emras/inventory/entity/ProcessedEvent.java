package com.emras.inventory.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
/**
 * Idempotency store for consumed Kafka events.
 *
 * Problem without this:
 *  Kafka may redeliver messages (at-least-once delivery guarantee).
 *  If our consumer crashes after processing but before acknowledging,
 *  Kafka redelivers the same message → we process it twice →
 *  stock reserved twice, stock deducted twice, etc.
 *
 * Solution:
 *  Before processing any Kafka message, check if its eventId is already here.
 *  If yes → skip (already processed) → acknowledge → done.
 *  If no  → process → save eventId here → acknowledge.
 *
 * This makes our consumer idempotent — safe to receive any event multiple times.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_events", schema = "schema_inventory",
        indexes = @Index(name = "idx_processed_event_id",
                columnList = "event_id", unique = true))
public class ProcessedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String eventId;
    @Column(name = "consumer_group", nullable = false, length = 100)
    private String consumerGroup;
    @Column(nullable = false, updatable = false)
    private Instant processedAt;
    @PrePersist
    protected void onCreate() {
        this.processedAt = Instant.now();
    }
}