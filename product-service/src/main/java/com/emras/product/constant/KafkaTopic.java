package com.emras.product.constant;

public final class KafkaTopic {
    private KafkaTopic() {}
    /** Published when a product is created or updated */
    public static final String PRODUCT_UPDATED    = "product.updated";
    /** Published when stock hits zero (consumed from Inventory Service) */
    public static final String PRODUCT_OUT_STOCK  = "product.out-of-stock";
}