package com.emras.inventory.dto.response;
import java.time.Instant;
public record InventoryItemResponse(
        Long    id,
        String  sku,
        String  productName,
        Long    productVariantId,
        int     quantity,
        int     reservedQuantity,
        int     availableQuantity,
        int     lowStockThreshold,
        boolean inStock,
        boolean lowStock,
        Instant updatedAt
) {}