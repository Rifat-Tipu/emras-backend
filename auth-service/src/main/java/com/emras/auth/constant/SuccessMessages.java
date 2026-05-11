package com.emras.auth.constant;
/**
 * All user-facing success messages for the Auth Service.
 */
public final class SuccessMessages {
    private SuccessMessages() {}
    public static final String REGISTERED             = "Registration successful. Please check your email to verify your account.";
    public static final String EMAIL_VERIFIED         = "Email verified successfully. You can now log in.";
    public static final String EMAIL_VERIFY_SENT      = "Verification email has been resent. Please check your inbox.";
    public static final String LOGGED_IN              = "Logged in successfully.";
    public static final String LOGGED_OUT             = "Logged out successfully.";
    public static final String OTP_SENT               = "OTP sent to your phone number. It will expire in 3 minutes.";
    public static final String OTP_VERIFIED           = "OTP verified successfully.";
    public static final String TOKEN_REFRESHED        = "Token refreshed successfully.";
    public static final String TOKEN_VALID            = "Token is valid.";
    public static final String PASSWORD_RESET_SENT    = "Password reset link has been sent to your email.";
    public static final String PASSWORD_RESET_SUCCESS = "Password has been reset successfully. Please log in with your new password.";
    public static final String PASSWORD_CHANGED       = "Password changed successfully.";
    public static final String ROLE_ASSIGNED          = "Role assigned successfully.";
}