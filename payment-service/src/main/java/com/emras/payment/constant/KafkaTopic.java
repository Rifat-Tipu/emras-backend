package com.emras.payment.constant;
public final class KafkaTopic {
    private KafkaTopic() {}
    // ── Consumed ──────────────────────────────────────────────────────────
    public static final String ORDER_CONFIRMED  = "order.confirmed";
    public static final String ORDER_CANCELLED  = "order.cancelled";
    // ── Published ─────────────────────────────────────────────────────────
    public static final String PAYMENT_SUCCESS  = "payment.success";
    public static final String PAYMENT_FAILED   = "payment.failed";
    public static final String PAYMENT_REFUNDED = "payment.refunded";
}