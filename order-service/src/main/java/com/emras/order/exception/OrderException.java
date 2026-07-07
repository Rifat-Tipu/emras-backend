package com.emras.order.exception;
import lombok.Getter;
@Getter
public class OrderException extends RuntimeException {
    private final String errorCode;
    public OrderException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}