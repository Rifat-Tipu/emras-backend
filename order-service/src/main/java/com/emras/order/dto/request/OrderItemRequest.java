package com.emras.order.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public record OrderItemRequest(
        @NotNull(message = "Product ID is required.")
        Long productId,
        Long productVariantId,
        @NotNull(message = "SKU is required.")
        String sku,
        @NotNull(message = "Quantity is required.")
        @Min(value = 1, message = "Quantity must be at least 1.")
        Integer quantity
) {}