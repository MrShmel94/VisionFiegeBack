package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto;


import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.BusBoarding;
import com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.dto.EmployeeCardResponseDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.route.dto.RouteResponseDTO;

import java.time.LocalDateTime;

public record BusBoardingResponseDTO(
        Long id,
        LocalDateTime boardedAt,
        LocalDateTime createdAt,
        EmployeeCardResponseDTO employeeCard,
        RouteResponseDTO route
) {
    public static BusBoardingResponseDTO fromEntity(BusBoarding b) {
        return new BusBoardingResponseDTO(
                b.getId(),
                b.getBoardedAt(),
                b.getCreatedAt(),
                b.getEmployeeCard() != null ? EmployeeCardResponseDTO.fromEntity(b.getEmployeeCard()) : null,
                b.getRoute() != null ? RouteResponseDTO.fromEntity(b.getRoute()) : null
        );
    }
}
