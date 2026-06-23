package com.emras.inventory.exception;
import lombok.Getter;

@Getter
public class InventoryException extends RuntimeException {
    private final String errorCode;
    public InventoryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}