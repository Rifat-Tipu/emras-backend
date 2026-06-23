package com.emras.inventory.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
/**
 * Immutable audit log for every stock movement.
 *
 * Every reservation, release, stock-in, and adjustment is recorded here.
 * This table is append-only — never updated or deleted.
 * Useful for reconciliation, dispute resolution, and analytics.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_audit_log", schema = "schema_inventory",
        indexes = {
                @Index(name = "idx_audit_sku",      columnList = "sku"),
                @Index(name = "idx_audit_order_id", columnList = "order_id"),
                @Index(name = "idx_audit_created",  columnList = "created_at")
        })
public class InventoryAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String sku;
    @Column(name = "order_id")
    private Long orderId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType movementType;
    /** Positive = stock increase, Negative = stock decrease */
    @Column(nullable = false)
    private int quantityChange;
    @Column(nullable = false)
    private int quantityBefore;
    @Column(nullable = false)
    private int quantityAfter;
    @Column(length = 255)
    private String notes;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(length = 100)
    private String createdBy;
    public enum MovementType {
        STOCK_IN,       // admin adds stock
        STOCK_ADJUST,   // admin manual adjustment (positive or negative)
        RESERVED,       // held for order
        RELEASED,       // reservation cancelled
        DEDUCTED        // payment confirmed, stock permanently reduced
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}