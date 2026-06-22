package com.emras.product.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "English name is required.")
        @Size(max = 100)
        String nameEn,
        @NotBlank(message = "Bangla name is required.")
        @Size(max = 100)
        String nameBn,
        @NotBlank(message = "Slug is required.")
        @Size(max = 150)
        String slug,
        Long parentId,
        String imageUrl
) {}