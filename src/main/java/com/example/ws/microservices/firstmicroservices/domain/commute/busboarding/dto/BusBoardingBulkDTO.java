package app.domain.busboarding.dto;

import java.time.LocalDateTime;

public record BusBoardingBulkDTO(
        String employeeCardNumber,
        Long routeId,
        LocalDateTime boardedAt
) {}
