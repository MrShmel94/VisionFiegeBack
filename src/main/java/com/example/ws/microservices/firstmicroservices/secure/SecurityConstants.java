package com.example.ws.microservices.firstmicroservices.secure;

public class SecurityConstants {

    //FOR TEST 4 HOURS, SIMPLE IMPL IS 10 MIN
    public static final long ACCESS_TOKEN_EXPIRATION = 240 * 60 * 1000L;
    public static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L;
    public static final int BUCKET_CAPACITY = 200;
    public static final int BUCKET_REFILL_TOKENS = 100;
    public static final int INACTIVITY_CLEANUP_MINUTES = 30;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/v1/users/sign-up";
    public static final String TOKEN_VERIFY_PREFIX = "/api/v1/users/verify-email/**";

}

