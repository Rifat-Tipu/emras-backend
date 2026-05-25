package com.emras.auth.service;
import com.emras.auth.dto.request.*;
import com.emras.auth.dto.response.AuthResponse;
import com.emras.auth.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request, HttpServletResponse response, HttpServletRequest httpRequest);

    void sendOtp(OtpSendRequest request);

    AuthResponse verifyOtp(OtpVerifyRequest request, HttpServletResponse response);

    AuthResponse refreshToken(String refreshTokenJti, HttpServletResponse response);

    void logout(String accessToken, String refreshTokenJti);

    void verifyEmail(String token);

    void resendVerificationEmail(String email);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request, Long userId);

    UserResponse validateToken(String token);
}