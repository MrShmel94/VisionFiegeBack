package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

import org.springframework.http.HttpStatus;

public class TokenGenerationException extends CustomException {
    public TokenGenerationException(String errorMessage) {
      super(errorMessage, HttpStatus.UNAUTHORIZED);
    }
}
