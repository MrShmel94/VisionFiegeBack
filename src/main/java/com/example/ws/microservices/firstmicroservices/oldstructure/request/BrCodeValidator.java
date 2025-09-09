package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BrCodeValidator implements ConstraintValidator<ValidBrCode, String> {

    private static final String BR_CODE_PATTERN = "^BR-\\d+$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return value.matches(BR_CODE_PATTERN);
    }
}