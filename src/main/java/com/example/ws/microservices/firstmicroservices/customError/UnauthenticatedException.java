package com.example.ws.microservices.firstmicroservices.customError;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
