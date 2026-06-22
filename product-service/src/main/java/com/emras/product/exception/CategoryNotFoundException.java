package com.emras.product.exception;
public class CategoryNotFoundException extends ProductException {
    public CategoryNotFoundException(String message) {
        super(message, "CATEGORY_NOT_FOUND");
    }
}