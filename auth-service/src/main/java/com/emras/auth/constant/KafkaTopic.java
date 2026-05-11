package com.emras.auth.constant;
/**
 * Kafka topic names produced by (or consumed by) the Auth Service.
 * Values must exactly match topic names across all services.
 */
public final class KafkaTopic {

    private KafkaTopic() {}
    /** Published when a new user registers. Consumed by: User Service, Notification Service. */
    public static final String USER_REGISTERED    = "user.registered";
    /** Published on every successful login (for audit). Consumed by: Analytics Service. */
    public static final String USER_LOGGED_IN     = "user.logged-in";
    /** Published when account is locked. Consumed by: Notification Service (alert email). */
    public static final String ACCOUNT_LOCKED     = "user.account-locked";
}