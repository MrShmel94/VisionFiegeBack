package app.domain.employeecard.dto;

import jakarta.validation.constraints.NotBlank;

public record EmployeeCardUpsertDTO(
        @NotBlank String cardNumber
) {}
