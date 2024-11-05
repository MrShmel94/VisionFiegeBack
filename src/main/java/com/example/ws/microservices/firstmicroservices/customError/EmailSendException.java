package com.example.ws.microservices.firstmicroservices.customError;

public class EmailSendException extends RuntimeException{

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }

}
