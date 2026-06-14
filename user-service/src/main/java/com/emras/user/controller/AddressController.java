package com.emras.user.controller;
import com.emras.user.constant.ApiEndpointConstant;
import com.emras.user.constant.SuccessMessages;
import com.emras.user.dto.request.CreateAddressRequest;
import com.emras.user.dto.response.AddressResponse;
import com.emras.user.model.ApiResponse;
import com.emras.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(ApiEndpointConstant.ADDRESSES)
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Manage delivery addresses")
public class AddressController {
    private final AddressService addressService;
    @GetMapping
    @Operation(summary = "Get all addresses for the authenticated user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ADDRESSES_FETCHED,
                addressService.getAddresses(userId),
                HttpStatus.OK));
    }
    @PostMapping
    @Operation(summary = "Add a new delivery address")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateAddressRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.ADDRESS_CREATED,
                addressService.createAddress(userId, request),
                HttpStatus.CREATED));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CreateAddressRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ADDRESS_UPDATED,
                addressService.updateAddress(userId, id, request),
                HttpStatus.OK));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        addressService.deleteAddress(userId, id);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ADDRESS_DELETED, HttpStatus.OK));
    }
    @PutMapping("/{id}/default")
    @Operation(summary = "Set an address as default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ADDRESS_SET_DEFAULT,
                addressService.setDefaultAddress(userId, id),
                HttpStatus.OK));
    }
}