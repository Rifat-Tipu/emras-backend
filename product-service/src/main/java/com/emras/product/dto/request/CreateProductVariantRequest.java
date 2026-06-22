package com.emras.product.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProductVariantRequest(
        @NotBlank(message = "SKU is required.")
        @Size(max = 100)
        String sku,
        @Size(max = 20)
        String size,
        @Size(max = 50)
        String color,
        BigDecimal additionalPrice
) {}