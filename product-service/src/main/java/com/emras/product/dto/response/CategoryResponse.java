package com.emras.product.dto.response;
import java.util.List;
public record CategoryResponse(
        Long   id,
        String nameEn,
        String nameBn,
        String slug,
        String imageUrl,
        Long   parentId,
        List<CategoryResponse> children
) {}