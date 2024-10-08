package com.example.ws.microservices.firstmicroservices.customError;

public class InvalidLoginRequestException extends RuntimeException{

    public InvalidLoginRequestException(String message) {
        super(message);
    }

    public InvalidLoginRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
