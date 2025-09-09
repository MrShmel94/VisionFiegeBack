package com.example.ws.microservices.firstmicroservices.oldstructure.request.attendance.gd;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ScheduleTemplateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScheduleTemplate {
    String message() default "Invalid schedule template";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
