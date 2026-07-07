package com.emras.product.dto.request;
import com.emras.product.entity.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(

        @NotNull(message = "Status is required.")
        ProductStatus status
) {}