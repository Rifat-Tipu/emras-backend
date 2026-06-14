package com.emras.user.service.impl;
import com.emras.user.constant.ErrorMessages;
import com.emras.user.dto.request.UpdateProfileRequest;
import com.emras.user.dto.response.UserProfileResponse;
import com.emras.user.entity.UserProfile;
import com.emras.user.exception.UserProfileNotFoundException;
import com.emras.user.mapper.UserProfileMapper;
import com.emras.user.repository.UserProfileRepository;
import com.emras.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository profileRepository;
    private final UserProfileMapper     profileMapper;
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(ErrorMessages.PROFILE_NOT_FOUND));
        return profileMapper.toResponse(profile);
    }
    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(ErrorMessages.PROFILE_NOT_FOUND));

        profileMapper.updateFromRequest(request, profile);
        UserProfile saved = profileRepository.save(profile);

        log.info("Profile updated for userId={}", userId);
        return profileMapper.toResponse(saved);
    }
    @Override
    @Transactional
    public void createProfileFromEvent(Long userId, String email, String phone) {
        if (profileRepository.existsByUserId(userId)) {
            log.warn("Profile already exists for userId={} — skipping creation", userId);
            return;
        }

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .phone(phone)
                .preferredLanguage(UserProfile.Language.EN)
                .build();

        profileRepository.save(profile);
        log.info("Profile created from Kafka event for userId={} email={}", userId, email);
    }
}