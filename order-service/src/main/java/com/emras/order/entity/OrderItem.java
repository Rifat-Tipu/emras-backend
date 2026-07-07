package com.emras.order.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
/**
 * One line item in an order.
 *
 * Stores productId, variantId, SKU, and a PRICE SNAPSHOT.
 * Price snapshot = price at the time of order, NOT current price.
 * This is critical — product prices change, orders must not.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items", schema = "schema_order",
        indexes = @Index(name = "idx_order_items_order", columnList = "order_id"))
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** References Product Service — no JPA join */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** References Product Service variant */
    @Column(name = "product_variant_id")
    private Long productVariantId;

    /** SKU used by Inventory Service for stock tracking */
    @Column(nullable = false, length = 100)
    private String sku;

    /** Price snapshot — locked at order time */
    @Column(nullable = false, length = 200)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private int quantity;

    /** unitPrice × quantity (or discountPrice × quantity if discounted) */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}