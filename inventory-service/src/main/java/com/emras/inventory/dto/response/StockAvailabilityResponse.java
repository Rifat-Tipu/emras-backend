package com.emras.inventory.dto.response;
public record StockAvailabilityResponse(
        String  sku,
        int     availableQuantity,
        boolean inStock,
        boolean lowStock
) {}