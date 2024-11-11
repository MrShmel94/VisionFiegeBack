package com.example.ws.microservices.firstmicroservices.customError;

public class InvalidIdFormatException extends RuntimeException {

    public InvalidIdFormatException(String message) {
        super(message);
    }

}