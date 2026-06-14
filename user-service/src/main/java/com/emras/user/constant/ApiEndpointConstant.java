package com.emras.user.constant;

public final class ApiEndpointConstant {

    private ApiEndpointConstant() {}

    public static final String BASE          = "/api/v1";
    public static final String USERS         = BASE + "/users";
    public static final String PROFILE       = USERS + "/profile";
    public static final String ADDRESSES     = USERS + "/addresses";
    public static final String ADDRESS_BY_ID = ADDRESSES + "/{id}";
    public static final String ADDRESS_DEFAULT = ADDRESSES + "/{id}/default";
    public static final String WISHLIST      = USERS + "/wishlist";
    public static final String WISHLIST_ITEM = WISHLIST + "/{productId}";
    public static final String PREFERENCES   = USERS + "/preferences";
}