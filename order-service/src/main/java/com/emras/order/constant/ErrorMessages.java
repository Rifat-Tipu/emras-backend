package com.emras.order.constant;
public final class ErrorMessages {
    private ErrorMessages() {}
    public static final String ORDER_NOT_FOUND       = "Order not found.";
    public static final String ORDER_CANCEL_DENIED   = "Only PENDING or CONFIRMED orders can be cancelled.";
    public static final String PRODUCT_NOT_AVAILABLE = "Product is not available for ordering.";
    public static final String ADDRESS_NOT_FOUND     = "Delivery address not found.";
    public static final String ACCESS_DENIED         = "You do not have permission to perform this action.";
    public static final String INTERNAL_ERROR        = "An unexpected error occurred. Please try again.";
}