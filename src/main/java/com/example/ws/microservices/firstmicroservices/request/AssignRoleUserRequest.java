package com.example.ws.microservices.firstmicroservices.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record AssignRoleUserRequest(
        @NotBlank String roleName,
        @NotBlank String expertis,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validTo
) {
}
