package com.emras.user.controller;
import com.emras.user.constant.ApiEndpointConstant;
import com.emras.user.constant.SuccessMessages;
import com.emras.user.dto.request.AddToWishlistRequest;
import com.emras.user.dto.response.WishlistItemResponse;
import com.emras.user.model.ApiResponse;
import com.emras.user.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(ApiEndpointConstant.WISHLIST)
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Manage product wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    @GetMapping
    @Operation(summary = "Get wishlist for authenticated user")
    public ResponseEntity<ApiResponse<List<WishlistItemResponse>>> getWishlist(
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.WISHLIST_FETCHED,
                wishlistService.getWishlist(userId),
                HttpStatus.OK));
    }
    @PostMapping
    @Operation(summary = "Add a product to wishlist")
    public ResponseEntity<ApiResponse<WishlistItemResponse>> addToWishlist(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddToWishlistRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.WISHLIST_ADDED,
                wishlistService.addToWishlist(userId, request),
                HttpStatus.CREATED));
    }
    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove a product from wishlist")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId) {

        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.WISHLIST_REMOVED, HttpStatus.OK));
    }
}