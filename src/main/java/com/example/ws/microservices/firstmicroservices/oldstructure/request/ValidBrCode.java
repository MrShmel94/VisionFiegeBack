package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BrCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBrCode {

    String message() default "BR-code must start with 'BR-' followed by digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
