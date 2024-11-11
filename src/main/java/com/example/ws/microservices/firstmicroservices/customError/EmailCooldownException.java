package com.example.ws.microservices.firstmicroservices.customError;

public class EmailCooldownException extends RuntimeException {

    public EmailCooldownException(String message) {
        super(message);
    }

}
