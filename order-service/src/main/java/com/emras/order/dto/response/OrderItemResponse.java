package com.emras.order.dto.response;
import java.math.BigDecimal;
public record OrderItemResponse(
        Long       id,
        Long       productId,
        Long       productVariantId,
        String     sku,
        String     productName,
        BigDecimal unitPrice,
        BigDecimal discountPrice,
        int        quantity,
        BigDecimal subtotal
) {}