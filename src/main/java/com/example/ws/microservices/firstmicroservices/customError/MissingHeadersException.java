package com.example.ws.microservices.firstmicroservices.customError;

public class MissingHeadersException extends Exception{
    public MissingHeadersException(String message) {
        super(message);
    }
}
