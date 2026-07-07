package com.emras.order.client.dto;
import java.math.BigDecimal;
/**
 * Subset of Product Service's ProductResponse.
 * Only fields Order Service needs — price, name, status.
 */
public record ProductResponse(
        Long       id,
        String     nameEn,
        String     nameBn,
        String     slug,
        BigDecimal price,
        BigDecimal discountPrice,
        String     status  // DRAFT, ACTIVE, ARCHIVED
) {}