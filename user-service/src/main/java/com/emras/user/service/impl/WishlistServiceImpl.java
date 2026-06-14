package com.emras.user.service.impl;
import com.emras.user.constant.ErrorMessages;
import com.emras.user.dto.request.AddToWishlistRequest;
import com.emras.user.dto.response.WishlistItemResponse;
import com.emras.user.entity.WishlistItem;
import com.emras.user.exception.UserServiceException;
import com.emras.user.exception.WishlistItemNotFoundException;
import com.emras.user.mapper.WishlistItemMapper;
import com.emras.user.repository.WishlistItemRepository;
import com.emras.user.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistItemRepository wishlistRepository;
    private final WishlistItemMapper     wishlistMapper;
    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        return wishlistMapper.toResponseList(
                wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }
    @Override
    @Transactional
    public WishlistItemResponse addToWishlist(Long userId, AddToWishlistRequest request) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, request.productId())) {
            throw new UserServiceException(
                    ErrorMessages.WISHLIST_ALREADY_EXISTS, "WISHLIST_ALREADY_EXISTS");
        }

        WishlistItem item = WishlistItem.builder()
                .userId(userId)
                .productId(request.productId())
                .build();

        return wishlistMapper.toResponse(wishlistRepository.save(item));
    }
    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new WishlistItemNotFoundException(ErrorMessages.WISHLIST_ITEM_NOT_FOUND);
        }
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        log.info("Product {} removed from wishlist for userId={}", productId, userId);
    }
}