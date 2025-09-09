package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

public class InvalidIdFormatException extends RuntimeException {

    public InvalidIdFormatException(String message) {
        super(message);
    }

}