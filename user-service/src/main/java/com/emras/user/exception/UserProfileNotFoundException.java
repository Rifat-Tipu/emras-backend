package com.emras.user.exception;
public class UserProfileNotFoundException extends UserServiceException {
    public UserProfileNotFoundException(String message) {
        super(message, "PROFILE_NOT_FOUND");
    }
}