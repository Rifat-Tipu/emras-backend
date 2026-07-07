package com.emras.order.client.dto;
import java.math.BigDecimal;
public record ProductVariantResponse(
        Long       id,
        String     sku,
        String     size,
        String     color,
        BigDecimal additionalPrice,
        Boolean    active
) {}