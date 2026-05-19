package com.emras.auth.controller;

import com.emras.auth.constant.ApiEndpointConstant;
import com.emras.auth.constant.SecurityConstant;
import com.emras.auth.constant.SuccessMessages;
import com.emras.auth.dto.request.*;
import com.emras.auth.dto.response.AuthResponse;
import com.emras.auth.dto.response.UserResponse;
import com.emras.auth.model.ApiResponse;
import com.emras.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping(ApiEndpointConstant.AUTH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, OTP, token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(SuccessMessages.REGISTERED, user, HttpStatus.CREATED)
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        AuthResponse auth = authService.login(request, httpResponse, httpRequest);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.LOGGED_IN, auth, HttpStatus.OK)
        );
    }

    @PostMapping("/login/otp/send")
    @Operation(summary = "Send OTP to phone number")
    public ResponseEntity<ApiResponse<Void>> sendOtp(
            @Valid @RequestBody OtpSendRequest request) {

        authService.sendOtp(request);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.OTP_SENT, HttpStatus.OK)
        );
    }

    @PostMapping("/login/otp/verify")
    @Operation(summary = "Verify OTP and receive access token")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request,
            HttpServletResponse httpResponse) {

        AuthResponse auth = authService.verifyOtp(request, httpResponse);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.OTP_VERIFIED, auth, HttpStatus.OK)
        );
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using HttpOnly cookie")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshJti = extractRefreshTokenFromCookie(request);
        AuthResponse auth = authService.refreshToken(refreshJti, response);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.TOKEN_REFRESHED, auth, HttpStatus.OK)
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout — invalidates tokens")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String accessToken = extractAccessToken(request);
        String refreshJti  = extractRefreshTokenFromCookie(request);
        authService.logout(accessToken, refreshJti);
        clearRefreshCookie(response);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.LOGGED_OUT, HttpStatus.OK)
        );
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate access token — used by API Gateway")
    public ResponseEntity<ApiResponse<UserResponse>> validate(HttpServletRequest request) {
        String token = extractAccessToken(request);
        UserResponse user = authService.validateToken(token);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.TOKEN_VALID, user, HttpStatus.OK)
        );
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email using token from verification link")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.EMAIL_VERIFIED, HttpStatus.OK)
        );
    }

    @PostMapping("/verify-email/resend")
    @Operation(summary = "Resend email verification link")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.EMAIL_VERIFY_SENT, HttpStatus.OK)
        );
    }

    @PostMapping("/password/forgot")
    @Operation(summary = "Request password reset link")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.PASSWORD_RESET_SENT, HttpStatus.OK)
        );
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password using token from email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.PASSWORD_RESET_SUCCESS, HttpStatus.OK)
        );
    }

    @PostMapping("/password/change")
    @Operation(summary = "Change password (requires valid JWT)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {

        Long userId = extractUserIdFromRequest(httpRequest);
        authService.changePassword(request, userId);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.PASSWORD_CHANGED, HttpStatus.OK)
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstant.AUTH_HEADER);
        if (header != null && header.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            return header.substring(SecurityConstant.TOKEN_PREFIX.length());
        }
        return null;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> SecurityConstant.REFRESH_TOKEN_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SecurityConstant.REFRESH_TOKEN_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader(SecurityConstant.USER_ID_HEADER);
        if (userId == null) return null;
        return Long.parseLong(userId);
    }
}