package com.emras.auth.constant;
/**
 * All user-facing error messages for the Auth Service.
 * Referenced in GlobalExceptionHandler and service layer.
 * Change a message here — it updates everywhere automatically.
 */
public final class ErrorMessages {
    private ErrorMessages() {}
    // ── Authentication ─────────────────────────────────────────────────────
    public static final String INVALID_CREDENTIALS    = "Invalid email or password.";
    public static final String ACCOUNT_LOCKED         = "Account is temporarily locked due to too many failed attempts. Please try again later.";
    public static final String ACCOUNT_DISABLED       = "Your account has been disabled. Please contact support.";
    public static final String EMAIL_NOT_VERIFIED     = "Please verify your email address before logging in.";
    // ── Token ──────────────────────────────────────────────────────────────
    public static final String TOKEN_EXPIRED          = "Your session has expired. Please log in again.";
    public static final String TOKEN_INVALID          = "Invalid or malformed token.";
    public static final String TOKEN_BLACKLISTED      = "This token has been invalidated. Please log in again.";
    public static final String REFRESH_TOKEN_INVALID  = "Invalid refresh token. Please log in again.";
    public static final String REFRESH_TOKEN_EXPIRED  = "Your session has expired. Please log in again.";
    // ── OTP ────────────────────────────────────────────────────────────────
    public static final String OTP_SENT_FAILED        = "Failed to send OTP. Please try again.";
    public static final String OTP_INVALID            = "Invalid OTP. Please check and try again.";
    public static final String OTP_EXPIRED            = "OTP has expired. Please request a new one.";
    public static final String OTP_MAX_ATTEMPTS       = "Too many invalid OTP attempts. Please request a new OTP.";
    // ── Registration ───────────────────────────────────────────────────────
    public static final String EMAIL_ALREADY_EXISTS   = "An account with this email address already exists.";
    public static final String PHONE_ALREADY_EXISTS   = "An account with this phone number already exists.";
    public static final String INVALID_PHONE_FORMAT   = "Invalid phone number. Please use a valid Bangladeshi number (e.g. 01712345678).";
    // ── Email Verification ─────────────────────────────────────────────────
    public static final String EMAIL_VERIFY_INVALID   = "Invalid or expired email verification link.";
    public static final String EMAIL_ALREADY_VERIFIED = "Your email address is already verified.";
    // ── Password ───────────────────────────────────────────────────────────
    public static final String PASSWORD_RESET_INVALID = "Invalid or expired password reset link.";
    public static final String PASSWORD_MISMATCH      = "Current password is incorrect.";
    public static final String PASSWORD_SAME_AS_OLD   = "New password must be different from your current password.";
    // ── Authorization ──────────────────────────────────────────────────────
    public static final String ACCESS_DENIED          = "You do not have permission to perform this action.";
    public static final String AUTHENTICATION_REQUIRED = "Authentication is required to access this resource.";
    // ── User ───────────────────────────────────────────────────────────────
    public static final String USER_NOT_FOUND         = "User not found.";
    public static final String ROLE_NOT_FOUND         = "Role not found.";
    // ── Generic ────────────────────────────────────────────────────────────
    public static final String VALIDATION_FAILED      = "Validation failed. Please check your input.";
    public static final String INTERNAL_ERROR         = "An unexpected error occurred. Please try again.";
}