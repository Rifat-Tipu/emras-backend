package com.emras.order.exception;
public class OrderNotFoundException extends OrderException {
    public OrderNotFoundException() {
        super("Order not found.", "ORDER_NOT_FOUND");
    }
}