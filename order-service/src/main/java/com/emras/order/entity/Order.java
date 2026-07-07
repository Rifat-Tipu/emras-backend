package com.emras.order.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Core order entity.
 *
 * Status flow:
 *  PENDING     → order created, waiting for inventory reservation
 *  CONFIRMED   → inventory reserved, waiting for payment
 *  PROCESSING  → payment initiated
 *  COMPLETED   → payment succeeded, order fulfilled
 *  CANCELLED   → cancelled by customer OR saga compensation
 *  FAILED      → unrecoverable error
 *
 * Why we store productName/price on the order item:
 *  Product prices change over time. The order must remember
 *  what the customer ACTUALLY paid, not the current price.
 *  This is called "price snapshot" — critical for receipts and disputes.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders", schema = "schema_order",
        indexes = {
                @Index(name = "idx_orders_user_id",   columnList = "user_id"),
                @Index(name = "idx_orders_status",    columnList = "status"),
                @Index(name = "idx_orders_created",   columnList = "created_at")
        })
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /** Total amount the customer pays */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /** Discount applied at checkout */
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /** Delivery charge */
    @Column(precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal deliveryCharge = BigDecimal.ZERO;

    /** Coupon code used — null if no coupon */
    @Column(length = 50)
    private String couponCode;

    /** Payment method selected by customer */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;

    /** Snapshot of delivery address at order time */
    @Column(nullable = false, length = 500)
    private String deliveryAddress;

    /** Reference to User Service address ID */
    @Column(name = "address_id")
    private Long addressId;

    /** Special instructions from customer */
    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderItem> items = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderStatusHistory> statusHistory = new HashSet<>();

    public enum OrderStatus {
        PENDING,     // created, awaiting inventory
        CONFIRMED,   // inventory reserved, awaiting payment
        PROCESSING,  // payment initiated
        COMPLETED,   // payment success, fulfilled
        CANCELLED,   // cancelled by user or saga
        FAILED       // unrecoverable error
    }

    public enum PaymentMethod {
        BKASH, NAGAD, ROCKET, SSLCOMMERZ, COD
    }

    /** Helper to add status history entry */
    public void addStatusHistory(OrderStatus newStatus, String note) {
        this.status = newStatus;
        this.statusHistory.add(OrderStatusHistory.builder()
                .order(this)
                .status(newStatus)
                .note(note)
                .build());
    }
}