package com.emras.payment.exception;

public class PaymentNotFoundException extends PaymentException {
    public PaymentNotFoundException() {
        super("Payment record not found.", "PAYMENT_NOT_FOUND");
    }
}