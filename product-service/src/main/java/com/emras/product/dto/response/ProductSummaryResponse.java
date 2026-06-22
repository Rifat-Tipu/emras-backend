package com.emras.product.dto.response;
import com.emras.product.entity.Product;
import java.math.BigDecimal;
/** Lightweight version for listing pages — no variants or full description */
public record ProductSummaryResponse(
        Long                  id,
        String                nameEn,
        String                nameBn,
        String                slug,
        BigDecimal            price,
        BigDecimal            discountPrice,
        Product.ProductStatus status,
        Boolean               featured,
        String                primaryImageUrl,
        String                categoryNameEn
) {}