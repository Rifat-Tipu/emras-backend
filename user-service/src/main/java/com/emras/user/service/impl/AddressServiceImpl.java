package com.emras.user.service.impl;
import com.emras.user.constant.ErrorMessages;
import com.emras.user.dto.request.CreateAddressRequest;
import com.emras.user.dto.response.AddressResponse;
import com.emras.user.entity.Address;
import com.emras.user.exception.AddressNotFoundException;
import com.emras.user.exception.UserServiceException;
import com.emras.user.mapper.AddressMapper;
import com.emras.user.repository.AddressRepository;
import com.emras.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private static final int MAX_ADDRESSES = 5;
    private final AddressRepository addressRepository;
    private final AddressMapper     addressMapper;
    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long userId) {
        return addressMapper.toResponseList(
                addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtAsc(userId));
    }
    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, CreateAddressRequest request) {
        if (addressRepository.countByUserId(userId) >= MAX_ADDRESSES) {
            throw new UserServiceException(ErrorMessages.ADDRESS_LIMIT_REACHED,
                    "ADDRESS_LIMIT_REACHED");
        }
        Address address = addressMapper.toEntity(request);
        address.setUserId(userId);

        if (request.isDefault()) {
            addressRepository.clearDefaultForUser(userId);
            address.setIsDefault(true);
        }
        Address saved = addressRepository.save(address);
        log.info("Address created for userId={}", userId);
        return addressMapper.toResponse(saved);
    }
    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId,
                                         CreateAddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(ErrorMessages.ADDRESS_NOT_FOUND));

        address.setLabel(request.label() != null ? request.label() : address.getLabel());
        address.setDivision(request.division());
        address.setDistrict(request.district());
        address.setThana(request.thana());
        address.setArea(request.area());
        address.setHouseNumber(request.houseNumber());
        address.setPostalCode(request.postalCode());

        if (request.isDefault()) {
            addressRepository.clearDefaultForUser(userId);
            address.setIsDefault(true);
        }
        return addressMapper.toResponse(addressRepository.save(address));
    }
    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(ErrorMessages.ADDRESS_NOT_FOUND));
        addressRepository.delete(address);
        log.info("Address {} deleted for userId={}", addressId, userId);
    }
    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(ErrorMessages.ADDRESS_NOT_FOUND));

        addressRepository.clearDefaultForUser(userId);
        address.setIsDefault(true);
        return addressMapper.toResponse(addressRepository.save(address));
    }
}