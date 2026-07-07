package com.emras.order.constant;
public final class ApiEndpointConstant {
    private ApiEndpointConstant() {}
    public static final String BASE             = "/api/v1";
    public static final String ORDERS           = BASE + "/orders";
    public static final String ORDER_BY_ID      = ORDERS + "/{id}";
    public static final String ORDER_CANCEL     = ORDERS + "/{id}/cancel";
    public static final String ORDER_HISTORY    = ORDERS + "/history";
    public static final String ADMIN            = BASE + "/admin";
    public static final String ADMIN_ORDERS     = ADMIN + "/orders";
    public static final String ADMIN_ORDER_BY_ID= ADMIN_ORDERS + "/{id}";
    public static final String ADMIN_ORDER_STATUS = ADMIN_ORDERS + "/{id}/status";
}