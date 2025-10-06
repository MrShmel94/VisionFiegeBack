package app.domain.route.dto;

import jakarta.validation.constraints.*;

public record RouteUpsertDTO(
        @NotBlank String name
) {}
