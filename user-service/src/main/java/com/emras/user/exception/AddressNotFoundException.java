package com.emras.user.exception;
public class AddressNotFoundException extends UserServiceException {
    public AddressNotFoundException(String message) {
        super(message, "ADDRESS_NOT_FOUND");
    }
}