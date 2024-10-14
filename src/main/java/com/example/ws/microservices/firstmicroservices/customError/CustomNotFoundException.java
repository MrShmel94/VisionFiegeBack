package com.example.ws.microservices.firstmicroservices.customError;

public class CustomNotFoundException extends RuntimeException {

    public CustomNotFoundException(String message) {
        super(message);
    }

}
