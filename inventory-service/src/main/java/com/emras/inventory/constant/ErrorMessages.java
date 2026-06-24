package com.emras.inventory.constant;
public final class ErrorMessages {
    private ErrorMessages() {}
    public static final String ITEM_NOT_FOUND         = "Inventory item not found for SKU: ";
    public static final String INSUFFICIENT_STOCK     = "Insufficient stock available.";
    public static final String OUT_OF_STOCK           = "This item is currently out of stock.";
    public static final String SKU_ALREADY_EXISTS     = "An inventory item with this SKU already exists.";
    public static final String INVALID_QUANTITY       = "Quantity must be greater than zero.";
    public static final String ACCESS_DENIED          = "You do not have permission to perform this action.";
    public static final String INTERNAL_ERROR         = "An unexpected error occurred. Please try again.";
}