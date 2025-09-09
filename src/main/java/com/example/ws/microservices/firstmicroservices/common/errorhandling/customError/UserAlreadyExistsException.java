package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends CustomException{

    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
