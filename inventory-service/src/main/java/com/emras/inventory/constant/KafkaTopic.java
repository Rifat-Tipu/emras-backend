package com.emras.inventory.constant;
public final class KafkaTopic {
    private KafkaTopic() {}
    // ── Consumed ──────────────────────────────────────────────────────────
    public static final String ORDER_CREATED      = "order.created";
    public static final String ORDER_CANCELLED    = "order.cancelled";
    public static final String PRODUCT_UPDATED    = "product.updated";
    // ── Published ─────────────────────────────────────────────────────────
    public static final String INVENTORY_RESERVED      = "inventory.reserved";
    public static final String INVENTORY_RESERVE_FAIL  = "inventory.reservation.failed";
    public static final String INVENTORY_RELEASED      = "inventory.released";
    public static final String INVENTORY_LOW_STOCK     = "inventory.low-stock";
    public static final String INVENTORY_OUT_OF_STOCK  = "inventory.out-of-stock";
}