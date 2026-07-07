package com.emras.order.constant;
public final class KafkaTopic {
    private KafkaTopic() {}
    // ── Published by Order Service ────────────────────────────────────────
    public static final String ORDER_CREATED    = "order.created";
    public static final String ORDER_CONFIRMED  = "order.confirmed";
    public static final String ORDER_CANCELLED  = "order.cancelled";
    public static final String ORDER_SHIPPED    = "order.shipped";
    public static final String ORDER_DELIVERED  = "order.delivered";
    // ── Consumed by Order Service ─────────────────────────────────────────
    public static final String INVENTORY_RESERVED     = "inventory.reserved";
    public static final String INVENTORY_RESERVE_FAIL = "inventory.reservation.failed";
    public static final String PAYMENT_SUCCESS        = "payment.success";
    public static final String PAYMENT_FAILED         = "payment.failed";
}