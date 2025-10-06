package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BusBoardingUpsertDTO(
        @NotNull LocalDateTime boardedAt,
        @NotNull Long employeeCardId,
        @NotNull Long routeId
) {}
