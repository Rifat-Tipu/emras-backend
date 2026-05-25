package com.emras.auth.service.impl;

import com.emras.auth.constant.CacheKeys;
import com.emras.auth.constant.ErrorMessages;
import com.emras.auth.constant.SecurityConstant;
import com.emras.auth.dto.request.*;
import com.emras.auth.dto.response.AuthResponse;
import com.emras.auth.dto.response.UserResponse;
import com.emras.auth.entity.RefreshToken;
import com.emras.auth.entity.Role;
import com.emras.auth.entity.User;
import com.emras.auth.exception.AuthException;
import com.emras.auth.exception.TokenException;
import com.emras.auth.exception.UserNotFoundException;
import com.emras.auth.kafka.producer.AuthEventProducer;
import com.emras.auth.repository.RefreshTokenRepository;
import com.emras.auth.repository.RoleRepository;
import com.emras.auth.repository.UserRepository;
import com.emras.auth.security.JwtProperties;
import com.emras.auth.security.JwtService;
import com.emras.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository         userRepository;
    private final RoleRepository         roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder        passwordEncoder;
    private final AuthenticationManager  authenticationManager;
    private final JwtService             jwtService;
    private final JwtProperties          jwtProperties;
    private final StringRedisTemplate    redisTemplate;
    private final AuthEventProducer      authEventProducer;

    // ── Register ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(ErrorMessages.EMAIL_ALREADY_EXISTS, "EMAIL_ALREADY_EXISTS");
        }
        if (request.phone() != null && userRepository.existsByPhone(request.phone())) {
            throw new AuthException(ErrorMessages.PHONE_ALREADY_EXISTS, "PHONE_ALREADY_EXISTS");
        }

        Role customerRole = roleRepository.findByName(Role.RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new AuthException(ErrorMessages.ROLE_NOT_FOUND, "ROLE_NOT_FOUND"));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .authProvider(User.AuthProvider.LOCAL)
                .emailVerified(false)
                .enabled(true)
                .roles(Set.of(customerRole))
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: id={} email={}", saved.getId(), saved.getEmail());

        authEventProducer.publishUserRegistered(saved.getId(), saved.getEmail(), saved.getPhone());

        return toUserResponse(saved);
    }

    // ── Email + Password Login ────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request,
                              HttpServletResponse response,
                              HttpServletRequest httpRequest) {
        String email = request.email();

        if (isAccountLocked(email)) {
            throw new AuthException(ErrorMessages.ACCOUNT_LOCKED, "ACCOUNT_LOCKED");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password()));
        } catch (BadCredentialsException e) {
            handleFailedLoginAttempt(email);
            throw new AuthException(ErrorMessages.INVALID_CREDENTIALS, "INVALID_CREDENTIALS");
        } catch (LockedException e) {
            throw new AuthException(ErrorMessages.ACCOUNT_LOCKED, "ACCOUNT_LOCKED");
        }

        redisTemplate.delete(CacheKeys.loginAttempts(email));

        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if (!user.isEmailVerified()) {
            throw new AuthException(ErrorMessages.EMAIL_NOT_VERIFIED, "EMAIL_NOT_VERIFIED");
        }

        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        AuthResponse authResponse = issueTokens(user, response, ipAddress, userAgent);

        authEventProducer.publishUserLoggedIn(user.getId(), user.getEmail(), ipAddress);
        log.info("User logged in: id={} email={}", user.getId(), user.getEmail());

        return authResponse;
    }

    // ── OTP Send ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public void sendOtp(OtpSendRequest request) {
        String phone = request.phone();

        userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        String otp = generateOtp();
        redisTemplate.opsForValue().set(CacheKeys.otpCode(phone), otp, Duration.ofMinutes(3));
        redisTemplate.delete(CacheKeys.otpAttempts(phone));

        // TODO: Send SMS via Notification Service
        log.info("OTP for {} : {} (REMOVE THIS LOG IN PRODUCTION)", phone, otp);
    }

    // ── OTP Verify ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request, HttpServletResponse response) {
        String phone = request.phone();
        String submittedOtp = request.otp();

        String attemptsStr = redisTemplate.opsForValue().get(CacheKeys.otpAttempts(phone));
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
        if (attempts >= 5) {
            throw new AuthException(ErrorMessages.OTP_MAX_ATTEMPTS, "OTP_MAX_ATTEMPTS");
        }

        String storedOtp = redisTemplate.opsForValue().get(CacheKeys.otpCode(phone));
        if (storedOtp == null) {
            throw new AuthException(ErrorMessages.OTP_EXPIRED, "OTP_EXPIRED");
        }
        if (!storedOtp.equals(submittedOtp)) {
            redisTemplate.opsForValue().increment(CacheKeys.otpAttempts(phone));
            redisTemplate.expire(CacheKeys.otpAttempts(phone), Duration.ofMinutes(3));
            throw new AuthException(ErrorMessages.OTP_INVALID, "OTP_INVALID");
        }

        redisTemplate.delete(CacheKeys.otpCode(phone));
        redisTemplate.delete(CacheKeys.otpAttempts(phone));

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        log.info("OTP login successful: userId={}", user.getId());
        return issueTokens(user, response, null, null);
    }

    // ── Refresh Token ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshTokenJti, HttpServletResponse response) {
        if (refreshTokenJti == null || refreshTokenJti.isBlank()) {
            throw new TokenException(ErrorMessages.REFRESH_TOKEN_INVALID, "REFRESH_TOKEN_INVALID");
        }

        String userIdStr = redisTemplate.opsForValue().get(CacheKeys.refreshToken(refreshTokenJti));
        if (userIdStr == null) {
            throw new TokenException(ErrorMessages.REFRESH_TOKEN_EXPIRED, "REFRESH_TOKEN_EXPIRED");
        }

        RefreshToken storedToken = refreshTokenRepository.findByJti(refreshTokenJti)
                .orElseThrow(() -> new TokenException(ErrorMessages.REFRESH_TOKEN_INVALID, "REFRESH_TOKEN_INVALID"));

        if (!storedToken.isValid()) {
            revokeAllUserRefreshTokens(storedToken.getUser());
            throw new TokenException(ErrorMessages.REFRESH_TOKEN_INVALID, "REFRESH_TOKEN_INVALID");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
        redisTemplate.delete(CacheKeys.refreshToken(refreshTokenJti));

        return issueTokens(storedToken.getUser(), response,
                storedToken.getIpAddress(), storedToken.getUserAgent());
    }

    // ── Logout ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void logout(String accessToken, String refreshTokenJti) {
        if (accessToken != null) {
            jwtService.blacklistToken(accessToken);
        }
        if (refreshTokenJti != null) {
            redisTemplate.delete(CacheKeys.refreshToken(refreshTokenJti));
            refreshTokenRepository.findByJti(refreshTokenJti).ifPresent(rt -> {
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            });
        }
        log.debug("User logged out");
    }

    // ── Email Verification ────────────────────────────────────────────────

    @Override
    @Transactional
    public void verifyEmail(String token) {
        String email = redisTemplate.opsForValue()
                .get(CacheKeys.emailVerificationToken(token));
        if (email == null) {
            throw new AuthException(ErrorMessages.EMAIL_VERIFY_INVALID, "EMAIL_VERIFY_INVALID");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        if (user.isEmailVerified()) {
            throw new AuthException(ErrorMessages.EMAIL_ALREADY_VERIFIED, "EMAIL_ALREADY_VERIFIED");
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        redisTemplate.delete(CacheKeys.emailVerificationToken(token));
        log.info("Email verified: userId={}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        if (user.isEmailVerified()) {
            throw new AuthException(ErrorMessages.EMAIL_ALREADY_VERIFIED, "EMAIL_ALREADY_VERIFIED");
        }
        storeAndSendVerificationToken(user);
    }

    // ── Forgot / Reset Password ───────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email());
        userOpt.ifPresent(user -> {
            String token = java.util.UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(
                    CacheKeys.passwordResetToken(token),
                    user.getEmail(),
                    Duration.ofMinutes(15)
            );
            // TODO: Publish Kafka event → Notification Service sends reset email
            log.info("Password reset token for userId={} : {} (REMOVE IN PROD)", user.getId(), token);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = redisTemplate.opsForValue()
                .get(CacheKeys.passwordResetToken(request.token()));
        if (email == null) {
            throw new AuthException(ErrorMessages.PASSWORD_RESET_INVALID, "PASSWORD_RESET_INVALID");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        revokeAllUserRefreshTokens(user);
        redisTemplate.delete(CacheKeys.passwordResetToken(request.token()));
        log.info("Password reset: userId={}", user.getId());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new AuthException(ErrorMessages.PASSWORD_MISMATCH, "PASSWORD_MISMATCH");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password changed: userId={}", userId);
    }

    // ── Validate Token ────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserResponse validateToken(String token) {
        var claims = jwtService.validateAccessToken(token);
        Long userId = claims.get("userId", Long.class);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        return toUserResponse(user);
    }

    // ── Private Helpers ───────────────────────────────────────────────────

    private AuthResponse issueTokens(User user, HttpServletResponse response,
                                     String ipAddress, String userAgent) {
        String roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.joining(","));

        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), roles);

        String refreshJti = jwtService.generateRefreshTokenJti();
        Instant refreshExpiry = Instant.now()
                .plusMillis(jwtProperties.getRefreshTokenExpirationMs());

        RefreshToken refreshToken = RefreshToken.builder()
                .jti(refreshJti)
                .user(user)
                .expiresAt(refreshExpiry)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .build();
        refreshTokenRepository.save(refreshToken);

        redisTemplate.opsForValue().set(
                CacheKeys.refreshToken(refreshJti),
                String.valueOf(user.getId()),
                Duration.ofMillis(jwtProperties.getRefreshTokenExpirationMs())
        );

        setRefreshTokenCookie(response, refreshJti);

        Set<String> roleSet = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return AuthResponse.of(
                accessToken,
                jwtProperties.getAccessTokenExpirationMs() / 1000,
                user.getId(),
                user.getEmail(),
                roleSet
        );
    }

    private void storeAndSendVerificationToken(User user) {
        String token = java.util.UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                CacheKeys.emailVerificationToken(token),
                user.getEmail(),
                Duration.ofHours(24)
        );
        // TODO: Publish Kafka event → Notification Service sends verification email
        log.info("Email verify token for userId={} : {} (REMOVE IN PROD)", user.getId(), token);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String jti) {
        Cookie cookie = new Cookie(SecurityConstant.REFRESH_TOKEN_COOKIE, jti);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);        // set true in production (HTTPS)
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge((int) (jwtProperties.getRefreshTokenExpirationMs() / 1000));
        response.addCookie(cookie);
    }

    private void handleFailedLoginAttempt(String email) {
        String key = CacheKeys.loginAttempts(email);
        Long attempts = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(30));

        if (attempts != null && attempts >= SecurityConstant.MAX_LOGIN_ATTEMPTS) {
            redisTemplate.opsForValue().set(
                    CacheKeys.accountLockout(email), "locked",
                    Duration.ofMinutes(SecurityConstant.LOCKOUT_MINUTES)
            );
            userRepository.findByEmail(email).ifPresent(user ->
                    authEventProducer.publishAccountLocked(user.getId(), email));
            log.warn("Account locked: {}", email);
        }
    }

    private boolean isAccountLocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(CacheKeys.accountLockout(email)));
    }

    private void revokeAllUserRefreshTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private UserResponse toUserResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.isEmailVerified(),
                roleNames,
                user.getCreatedAt()
        );
    }
}