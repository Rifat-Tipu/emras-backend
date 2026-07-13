package com.emras.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Records every payment attempt for an order.
 *
 * One order can have multiple payment records if:
 *  - First attempt fails → customer retries → second record
 *  - Partial refund issued → refund record created
 *
 * Gateway simulation rules for local dev:
 *  - COD → always SUCCESS immediately
 *  - bKash/Nagad/Rocket → SUCCESS if amount is even, FAILED if odd
 *  - SSLCommerz → always SUCCESS (card payment simulation)
 *
 * In production these would make real HTTP calls to payment gateway APIs.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments", schema = "schema_payment",
        indexes = {
                @Index(name = "idx_payment_order_id",      columnList = "order_id"),
                @Index(name = "idx_payment_status",        columnList = "status"),
                @Index(name = "idx_payment_transaction_id",columnList = "transaction_id")
        })
public class Payment extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private PaymentMethod method;

    /**
     * Transaction ID returned by the payment gateway.
     * For COD: generated internally.
     * For bKash/Nagad: returned by their API.
     */
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /** Raw response from gateway — stored for audit/dispute resolution */
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(length = 500)
    private String failureReason;

    /** When the gateway confirmed payment */
    private Instant paidAt;

    /** When refund was processed */
    private Instant refundedAt;

    @Column(precision = 12, scale = 2)
    private BigDecimal refundAmount;

    public enum PaymentStatus {
        PENDING,    // created, awaiting gateway response
        SUCCESS,    // gateway confirmed payment
        FAILED,     // gateway rejected or timed out
        REFUNDED,   // full refund processed
        PARTIAL_REFUND // partial refund processed
    }

    public enum PaymentMethod {
        BKASH, NAGAD, ROCKET, SSLCOMMERZ, COD
    }
}