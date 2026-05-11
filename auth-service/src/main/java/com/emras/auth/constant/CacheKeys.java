package com.emras.auth.constant;
/**
 * All Redis key patterns for the Auth Service.
 * Use these methods everywhere — never build key strings inline.
 *
 * Convention: {service}:{purpose}:{identifier}
 */
public final class CacheKeys {
    private CacheKeys() {}
    /** OTP code for phone-based login. TTL = 3 minutes. */
    public static String otpCode(String phone) {
        return "auth:otp:" + phone;
    }
    /** Attempt counter for OTP verification. TTL = 3 minutes. */
    public static String otpAttempts(String phone) {
        return "auth:otp:attempts:" + phone;
    }
    /** Refresh token whitelist. TTL = 7 days. Value = userId. */
    public static String refreshToken(String tokenId) {
        return "auth:refresh:" + tokenId;
    }
    /** Password reset token. TTL = 15 minutes. */
    public static String passwordResetToken(String token) {
        return "auth:pwd-reset:" + token;
    }
    /** Email verification token. TTL = 24 hours. */
    public static String emailVerificationToken(String token) {
        return "auth:email-verify:" + token;
    }
    /** Failed login attempt counter. TTL = 30 minutes after last attempt. */
    public static String loginAttempts(String email) {
        return "auth:login:attempts:" + email;
    }
    /** Account lockout flag. TTL = lockout duration. */
    public static String accountLockout(String email) {
        return "auth:lockout:" + email;
    }
    /** Blacklisted access tokens (on logout). TTL = remaining token TTL. */
    public static String blacklistedToken(String jti) {
        return "auth:token:blacklist:" + jti;
    }
}