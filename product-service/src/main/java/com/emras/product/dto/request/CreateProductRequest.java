package com.emras.product.dto.request;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "English name is required.")
        @Size(max = 200)
        String nameEn,
        @NotBlank(message = "Bangla name is required.")
        @Size(max = 200)
        String nameBn,
        String descriptionEn,
        String descriptionBn,
        @NotBlank(message = "Slug is required.")
        @Size(max = 250)
        String slug,
        @NotNull(message = "Price is required.")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
        BigDecimal price,
        BigDecimal discountPrice,
        Long categoryId,
        Boolean featured
) {}