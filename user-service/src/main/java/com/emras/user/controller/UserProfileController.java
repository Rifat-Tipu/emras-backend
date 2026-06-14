package com.emras.user.controller;
import com.emras.user.constant.ApiEndpointConstant;
import com.emras.user.constant.SuccessMessages;
import com.emras.user.dto.request.UpdateProfileRequest;
import com.emras.user.dto.response.UserProfileResponse;
import com.emras.user.model.ApiResponse;
import com.emras.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpointConstant.PROFILE)
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Get and update customer profile")
public class UserProfileController {
    private final UserProfileService profileService;
    @GetMapping
    @Operation(summary = "Get authenticated user's profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PROFILE_FETCHED,
                profileService.getProfile(userId),
                HttpStatus.OK));
    }
    @PutMapping
    @Operation(summary = "Update authenticated user's profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PROFILE_UPDATED,
                profileService.updateProfile(userId, request),
                HttpStatus.OK));
    }
}