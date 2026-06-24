package com.emras.inventory.exception;

public class InventoryItemNotFoundException extends InventoryException {
    public InventoryItemNotFoundException(String sku) {
        super("Inventory item not found for SKU: " + sku, "INVENTORY_NOT_FOUND");
    }
}