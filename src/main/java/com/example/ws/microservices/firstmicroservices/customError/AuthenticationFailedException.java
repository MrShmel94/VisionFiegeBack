package com.example.ws.microservices.firstmicroservices.customError;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends CustomException{
    public AuthenticationFailedException(String errorMessage) {
        super(errorMessage, HttpStatus.UNAUTHORIZED);
    }
}
