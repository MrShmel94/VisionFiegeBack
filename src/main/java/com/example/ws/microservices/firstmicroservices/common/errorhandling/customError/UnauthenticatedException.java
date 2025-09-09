package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
