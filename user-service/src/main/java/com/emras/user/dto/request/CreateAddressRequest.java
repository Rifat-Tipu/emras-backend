package com.emras.user.dto.request;
import com.emras.user.entity.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CreateAddressRequest(
        Address.AddressLabel label,
        @NotBlank(message = "Division is required.")
        @Size(max = 50)
        String division,
        @NotBlank(message = "District is required.")
        @Size(max = 50)
        String district,
        @NotBlank(message = "Thana is required.")
        @Size(max = 100)
        String thana,
        @Size(max = 255)
        String area,
        @NotBlank(message = "House number is required.")
        @Size(max = 255)
        String houseNumber,
        @Size(max = 10)
        String postalCode,
        boolean isDefault
) {}