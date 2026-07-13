package com.emras.payment.constant;

public final class ErrorMessages {

    private ErrorMessages() {}

    public static final String PAYMENT_NOT_FOUND    = "Payment record not found.";
    public static final String ALREADY_PAID         = "This order has already been paid.";
    public static final String PAYMENT_FAILED       = "Payment processing failed.";
    public static final String REFUND_NOT_ALLOWED   = "Refund is only allowed for completed payments.";
    public static final String ACCESS_DENIED        = "You do not have permission to perform this action.";
}