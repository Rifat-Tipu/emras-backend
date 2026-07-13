package com.emras.payment.constant;

public final class ApiEndpointConstant {

    private ApiEndpointConstant() {}

    public static final String BASE                  = "/api/v1";
    public static final String PAYMENTS              = BASE + "/payments";
    public static final String PAYMENT_BY_ORDER      = PAYMENTS + "/order/{orderId}";
    public static final String PAYMENT_BY_ID         = PAYMENTS + "/{id}";

    // Webhook endpoints — called by payment gateways
    public static final String WEBHOOK               = BASE + "/webhooks";
    public static final String BKASH_WEBHOOK         = WEBHOOK + "/bkash";
    public static final String NAGAD_WEBHOOK         = WEBHOOK + "/nagad";
    public static final String SSLCOMMERZ_WEBHOOK    = WEBHOOK + "/sslcommerz";

    public static final String ADMIN                 = BASE + "/admin";
    public static final String ADMIN_PAYMENTS        = ADMIN + "/payments";
    public static final String ADMIN_REFUND          = ADMIN_PAYMENTS + "/{id}/refund";
}