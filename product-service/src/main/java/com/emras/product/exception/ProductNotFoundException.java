package com.emras.product.exception;
public class ProductNotFoundException extends ProductException {
    public ProductNotFoundException(String message) {
        super(message, "PRODUCT_NOT_FOUND");
    }
}