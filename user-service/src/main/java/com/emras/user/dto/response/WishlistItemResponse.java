package com.emras.user.dto.response;
import java.time.Instant;
public record WishlistItemResponse(
        Long    id,
        Long    productId,
        Instant addedAt
) {}