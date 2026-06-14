package com.emras.user.constant;

public final class KafkaTopic {
    private KafkaTopic() {}
    /** Consumed from Auth Service — triggers automatic profile creation */
    public static final String USER_REGISTERED = "user.registered";
}