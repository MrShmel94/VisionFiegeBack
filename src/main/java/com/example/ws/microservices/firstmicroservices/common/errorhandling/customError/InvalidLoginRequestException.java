package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

import org.springframework.http.HttpStatus;

public class InvalidLoginRequestException extends CustomException{

    public InvalidLoginRequestException(String message, HttpStatus status) {
        super(message, status);
    }

}
