package com.emras.user.exception;
public class WishlistItemNotFoundException extends UserServiceException {
    public WishlistItemNotFoundException(String message) {
        super(message, "WISHLIST_ITEM_NOT_FOUND");
    }
}