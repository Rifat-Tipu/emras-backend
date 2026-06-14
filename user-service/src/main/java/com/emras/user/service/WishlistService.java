package com.emras.user.service;
import com.emras.user.dto.request.AddToWishlistRequest;
import com.emras.user.dto.response.WishlistItemResponse;
import java.util.List;

public interface WishlistService {
    List<WishlistItemResponse> getWishlist(Long userId);
    WishlistItemResponse addToWishlist(Long userId, AddToWishlistRequest request);
    void removeFromWishlist(Long userId, Long productId);
}