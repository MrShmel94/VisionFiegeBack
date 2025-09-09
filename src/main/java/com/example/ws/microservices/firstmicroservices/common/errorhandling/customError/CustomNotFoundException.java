package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

public class CustomNotFoundException extends RuntimeException {

    public CustomNotFoundException(String message) {
        super(message);
    }

}
