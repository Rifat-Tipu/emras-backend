package com.emras.order.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
/**
 * Append-only log of every status change on an order.
 * Useful for customer support, dispute resolution, and analytics.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_status_history", schema = "schema_order",
        indexes = @Index(name = "idx_history_order", columnList = "order_id"))
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Order.OrderStatus status;

    @Column(length = 255)
    private String note;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}