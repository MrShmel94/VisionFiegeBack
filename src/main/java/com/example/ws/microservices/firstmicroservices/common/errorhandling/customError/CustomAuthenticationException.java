package com.example.ws.microservices.firstmicroservices.common.errorhandling.customError;

import org.springframework.http.HttpStatus;

public class CustomAuthenticationException extends CustomException {

  public CustomAuthenticationException(String message, HttpStatus status) {
    super(message, status);
  }

}
