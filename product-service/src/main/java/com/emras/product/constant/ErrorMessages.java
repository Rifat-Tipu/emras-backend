package com.emras.product.constant;

public final class ErrorMessages {
    private ErrorMessages() {}
    public static final String PRODUCT_NOT_FOUND     = "Product not found.";
    public static final String CATEGORY_NOT_FOUND    = "Category not found.";
    public static final String VARIANT_NOT_FOUND     = "Product variant not found.";
    public static final String SLUG_ALREADY_EXISTS   = "A product with this slug already exists.";
    public static final String ACCESS_DENIED         = "You do not have permission to perform this action.";
    public static final String VALIDATION_FAILED     = "Validation failed. Please check your input.";
    public static final String INTERNAL_ERROR        = "An unexpected error occurred. Please try again.";
}