package com.example.ws.microservices.firstmicroservices.secure;

public class SecurityConstants {

    //public static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000L;
    public static final long ACCESS_TOKEN_EXPIRATION = 60 * 1000L;
    public static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/v1/users/sign-up";
}

