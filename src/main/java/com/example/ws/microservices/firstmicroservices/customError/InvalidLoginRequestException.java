package com.example.ws.microservices.firstmicroservices.customError;

import org.springframework.http.HttpStatus;

public class InvalidLoginRequestException extends CustomException{

    public InvalidLoginRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidLoginRequestException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }

}
