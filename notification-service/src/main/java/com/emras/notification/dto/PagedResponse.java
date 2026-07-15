package com.emras.notification.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int     page,
        int     size,
        long    totalElements,
        int     totalPages,
        boolean last,
        long    unreadCount
) {}