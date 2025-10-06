package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto;

import java.time.LocalDateTime;

public record BusBoardingBulkDTO(
        String employeeCardNumber,
        Long routeId,
        LocalDateTime boardedAt
) {}
