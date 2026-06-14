package com.emras.user.service;
import com.emras.user.dto.request.CreateAddressRequest;
import com.emras.user.dto.response.AddressResponse;
import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddresses(Long userId);
    AddressResponse createAddress(Long userId, CreateAddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, CreateAddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    AddressResponse setDefaultAddress(Long userId, Long addressId);
}