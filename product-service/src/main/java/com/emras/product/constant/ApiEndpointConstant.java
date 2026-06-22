package com.emras.product.constant;

public final class ApiEndpointConstant {
    private ApiEndpointConstant() {}
    public static final String BASE     = "/api/v1";
    // ── Public endpoints (no auth required) ───────────────────────────────
    public static final String PRODUCTS         = BASE + "/products";
    public static final String PRODUCT_BY_ID    = PRODUCTS + "/{id}";
    public static final String PRODUCT_BY_SLUG  = PRODUCTS + "/slug/{slug}";
    public static final String CATEGORIES       = BASE + "/categories";
    public static final String CATEGORY_BY_ID   = CATEGORIES + "/{id}";
    // ── Admin endpoints (ROLE_ADMIN required) ─────────────────────────────
    public static final String ADMIN                    = BASE + "/admin";
    public static final String ADMIN_PRODUCTS           = ADMIN + "/products";
    public static final String ADMIN_PRODUCT_BY_ID      = ADMIN_PRODUCTS + "/{id}";
    public static final String ADMIN_PRODUCT_STATUS     = ADMIN_PRODUCTS + "/{id}/status";
    public static final String ADMIN_PRODUCT_VARIANTS   = ADMIN_PRODUCTS + "/{productId}/variants";
    public static final String ADMIN_PRODUCT_IMAGES     = ADMIN_PRODUCTS + "/{productId}/images";
    public static final String ADMIN_CATEGORIES         = ADMIN + "/categories";
    public static final String ADMIN_CATEGORY_BY_ID     = ADMIN_CATEGORIES + "/{id}";
}