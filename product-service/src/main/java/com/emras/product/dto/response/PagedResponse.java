package com.emras.product.dto.response;
import java.util.List;
/** Standard paginated response wrapper */
public record PagedResponse<T>(
        List<T> content,
        int     page,
        int     size,
        long    totalElements,
        int     totalPages,
        boolean last
) {}