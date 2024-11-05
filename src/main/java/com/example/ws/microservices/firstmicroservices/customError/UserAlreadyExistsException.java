package com.example.ws.microservices.firstmicroservices.customError;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends CustomException{

    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
