package app.domain.busboarding.dto;

import app.domain.busboarding.BusBoarding;
import app.domain.employeecard.dto.EmployeeCardResponseDTO;
import app.domain.route.dto.RouteResponseDTO;

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
