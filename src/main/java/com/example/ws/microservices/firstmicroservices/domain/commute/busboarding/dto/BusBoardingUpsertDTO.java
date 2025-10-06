package app.domain.busboarding.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BusBoardingUpsertDTO(
        @NotNull LocalDateTime boardedAt,
        @NotNull Long employeeCardId,
        @NotNull Long routeId
) {}
