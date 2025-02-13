package com.example.ws.microservices.firstmicroservices.customError;

import org.springframework.http.HttpStatus;

public class InvalidLoginRequestException extends CustomException{

    public InvalidLoginRequestException(String message, HttpStatus status) {
        super(message, status);
    }

}
