package com.example.ws.microservices.firstmicroservices.customError;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends CustomException{

    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
