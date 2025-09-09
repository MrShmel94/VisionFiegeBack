package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends CustomException{
    public AuthenticationFailedException(String errorMessage) {
        super(errorMessage, HttpStatus.UNAUTHORIZED);
    }
}
