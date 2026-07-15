package com.emras.notification.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_events", schema = "schema_notification",
        indexes = @Index(name = "idx_notif_processed_event",
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