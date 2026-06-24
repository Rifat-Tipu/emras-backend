package com.emras.inventory.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CreateInventoryItemRequest(
        @NotBlank(message = "SKU is required.")
        @Size(max = 100)
        String sku,
        @Size(max = 200)
        String productName,
        Long productVariantId,
        @Min(value = 0, message = "Initial quantity cannot be negative.")
        int initialQuantity,

        @Min(value = 1, message = "Low stock threshold must be at least 1.")
        int lowStockThreshold
) {}