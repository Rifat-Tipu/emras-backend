package com.emras.notification.constant;

public final class ApiEndpointConstant {
    private ApiEndpointConstant() {}
    public static final String BASE                  = "/api/v1";
    public static final String NOTIFICATIONS         = BASE + "/notifications";
    public static final String MY_NOTIFICATIONS      = NOTIFICATIONS + "/me";
    public static final String NOTIFICATION_BY_ID    = NOTIFICATIONS + "/{id}";
    public static final String MARK_READ             = NOTIFICATIONS + "/{id}/read";
    public static final String MARK_ALL_READ         = NOTIFICATIONS + "/read-all";
}