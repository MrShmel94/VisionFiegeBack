package com.example.ws.microservices.firstmicroservices.domain.commute.route.dto;

import jakarta.validation.constraints.*;

public record RouteUpsertDTO(
        @NotBlank String name
) {}
