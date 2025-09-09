package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

public class MissingHeadersException extends Exception{
    public MissingHeadersException(String message) {
        super(message);
    }
}
