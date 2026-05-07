package com.emras.auth.constant;
/**
 * All API endpoint paths for the Auth Service.
 * Controllers use these constants in @RequestMapping — never raw strings.
 */
public final class ApiEndpointConstant {
    private ApiEndpointConstant() {}
    public static final String BASE           = "/api/v1";
    public static final String AUTH           = BASE + "/auth";
    // Registration & login
    public static final String REGISTER       = AUTH + "/register";
    public static final String LOGIN          = AUTH + "/login";
    public static final String LOGIN_OTP_SEND = AUTH + "/login/otp/send";
    public static final String LOGIN_OTP_VERIFY = AUTH + "/login/otp/verify";
    // Token management
    public static final String REFRESH_TOKEN  = AUTH + "/refresh";
    public static final String LOGOUT         = AUTH + "/logout";
    public static final String VALIDATE_TOKEN = AUTH + "/validate";
    // Email verification
    public static final String VERIFY_EMAIL   = AUTH + "/verify-email";
    public static final String RESEND_VERIFY  = AUTH + "/verify-email/resend";
    // Password
    public static final String FORGOT_PASSWORD  = AUTH + "/password/forgot";
    public static final String RESET_PASSWORD   = AUTH + "/password/reset";
    public static final String CHANGE_PASSWORD  = AUTH + "/password/change";
    // OAuth2 — handled by Spring Security filter, listed here for documentation
    public static final String OAUTH2_CALLBACK  = AUTH + "/oauth2/callback/{provider}";
    // Admin — role management
    public static final String ASSIGN_ROLE      = AUTH + "/admin/users/{userId}/role";
}