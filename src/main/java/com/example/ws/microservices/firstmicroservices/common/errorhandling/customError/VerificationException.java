package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

import org.springframework.http.HttpStatus;

public class VerificationException extends CustomException{
    public VerificationException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
