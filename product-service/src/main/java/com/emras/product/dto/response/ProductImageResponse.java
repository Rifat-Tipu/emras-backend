package com.emras.product.dto.response;
public record ProductImageResponse(
        Long    id,
        String  url,
        String  altText,
        Boolean isPrimary,
        Integer displayOrder
) {}