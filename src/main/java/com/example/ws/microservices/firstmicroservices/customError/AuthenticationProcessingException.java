package com.example.ws.microservices.firstmicroservices.customError;

public class AuthenticationProcessingException extends RuntimeException  {

    public AuthenticationProcessingException(String message) {
        super(message);
    }

    public AuthenticationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
