package com.emras.user.dto.request;
import jakarta.validation.constraints.NotNull;
public record AddToWishlistRequest(
        @NotNull(message = "Product ID is required.")
        Long productId
) {}