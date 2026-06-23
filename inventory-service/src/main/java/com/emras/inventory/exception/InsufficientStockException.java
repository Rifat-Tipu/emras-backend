package com.emras.inventory.exception;

public class InsufficientStockException extends InventoryException {
    public InsufficientStockException(String sku, int requested, int available) {
        super(String.format(
                        "Insufficient stock for SKU %s. Requested: %d, Available: %d",
                        sku, requested, available),
                "INSUFFICIENT_STOCK");
    }
}