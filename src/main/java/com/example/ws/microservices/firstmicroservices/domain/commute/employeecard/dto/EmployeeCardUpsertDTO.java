package com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.dto;

import jakarta.validation.constraints.NotBlank;

public record EmployeeCardUpsertDTO(
        @NotBlank String cardNumber
) {}
