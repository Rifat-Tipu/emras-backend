package com.emras.inventory.entity;
import jakarta.persistence.*;
import lombok.*;
/**
 * Tracks stock for one SKU (Stock Keeping Unit).
 *
 * One SKU = one size/color combination of one product.
 * Example: "EMRAS-SHIRT-WHITE-M" = White shirt, Medium size.
 *
 * Optimistic Locking (@Version):
 *  When two threads read the same row simultaneously (both see quantity=1),
 *  the first UPDATE succeeds and increments the version.
 *  The second UPDATE finds a version mismatch → throws
 *  ObjectOptimisticLockingFailureException → caught → reservation fails → Kafka event sent.
 *  This guarantees we never oversell.
 *
 * Available quantity = quantity - reservedQuantity
 *  quantity         = total physical stock in warehouse
 *  reservedQuantity = held for orders currently in checkout/payment
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_items", schema = "schema_inventory",
        indexes = @Index(name = "idx_inventory_sku", columnList = "sku", unique = true))
public class InventoryItem extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Must match a SKU in Product Service's product_variants table */
    @Column(nullable = false, unique = true, length = 100)
    private String sku;
    /** Human-readable product name for admin display */
    @Column(length = 200)
    private String productName;
    /** Reference to Product Service — no JPA join (different schema) */
    @Column(name = "product_variant_id")
    private Long productVariantId;
    /** Total physical stock in warehouse */
    @Column(nullable = false)
    @Builder.Default
    private int quantity = 0;
    /** Stock held for active orders awaiting payment */
    @Column(nullable = false)
    @Builder.Default
    private int reservedQuantity = 0;
    /**
     * Kafka alert is published when availableQuantity drops to this number.
     * Default: 5 units triggers a low-stock alert to admin.
     */
    @Column(nullable = false)
    @Builder.Default
    private int lowStockThreshold = 5;
    /**
     * Optimistic locking version field.
     * JPA auto-increments this on every UPDATE.
     * Concurrent writes cause ObjectOptimisticLockingFailureException.
     */
    @Version
    private Long version;

    /** Computed — how much can actually be sold right now */
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
    public boolean isInStock() {
        return getAvailableQuantity() > 0;
    }
    public boolean isLowStock() {
        return getAvailableQuantity() <= lowStockThreshold && isInStock();
    }
}