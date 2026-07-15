package com.emras.notification.constant;

public final class KafkaTopic {

    private KafkaTopic() {}

    public static final String USER_REGISTERED        = "user.registered";
    public static final String ORDER_CONFIRMED        = "order.confirmed";
    public static final String ORDER_CANCELLED        = "order.cancelled";
    public static final String PAYMENT_SUCCESS        = "payment.success";
    public static final String PAYMENT_FAILED         = "payment.failed";
    public static final String PAYMENT_REFUNDED       = "payment.refunded";
    public static final String INVENTORY_LOW_STOCK    = "inventory.low-stock";
    public static final String INVENTORY_OUT_OF_STOCK = "inventory.out-of-stock";
}