package com.emras.user.constant;

public final class ErrorMessages {
    private ErrorMessages() {}
    public static final String PROFILE_NOT_FOUND     = "User profile not found.";
    public static final String ADDRESS_NOT_FOUND     = "Address not found.";
    public static final String ADDRESS_LIMIT_REACHED = "Maximum of 5 addresses allowed per account.";
    public static final String WISHLIST_ALREADY_EXISTS = "This product is already in your wishlist.";
    public static final String WISHLIST_ITEM_NOT_FOUND = "Product not found in your wishlist.";
    public static final String ACCESS_DENIED         = "You do not have permission to perform this action.";
    public static final String VALIDATION_FAILED     = "Validation failed. Please check your input.";
    public static final String INTERNAL_ERROR        = "An unexpected error occurred. Please try again.";
}