package com.emras.user.dto.response;
import com.emras.user.entity.Address;
public record AddressResponse(
        Long   id,
        Address.AddressLabel label,
        String division,
        String district,
        String thana,
        String area,
        String houseNumber,
        String postalCode,
        boolean isDefault
) {}