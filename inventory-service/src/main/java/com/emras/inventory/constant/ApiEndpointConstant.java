package com.emras.inventory.constant;
public final class ApiEndpointConstant {
    private ApiEndpointConstant() {}
    public static final String BASE              = "/api/v1";
    public static final String INVENTORY         = BASE + "/inventory";
    public static final String STOCK_BY_SKU      = INVENTORY + "/{sku}";
    public static final String STOCK_CHECK       = INVENTORY + "/{sku}/availability";

    public static final String ADMIN             = BASE + "/admin";
    public static final String ADMIN_INVENTORY   = ADMIN + "/inventory";
    public static final String ADMIN_STOCK_IN    = ADMIN_INVENTORY + "/{sku}/stock-in";
    public static final String ADMIN_STOCK_ADJUST= ADMIN_INVENTORY + "/{sku}/adjust";
    public static final String ADMIN_BULK_CREATE = ADMIN_INVENTORY + "/bulk";
}