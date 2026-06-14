package com.emras.user.service;
import com.emras.user.dto.request.UpdateProfileRequest;
import com.emras.user.dto.response.UserProfileResponse;
public interface UserProfileService {

    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    void createProfileFromEvent(Long userId, String email, String phone);
}