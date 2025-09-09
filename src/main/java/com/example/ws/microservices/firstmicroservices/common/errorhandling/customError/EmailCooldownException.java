package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

public class EmailCooldownException extends RuntimeException {

    public EmailCooldownException(String message) {
        super(message);
    }

}
