package app.domain.employeecard.dto;

import app.domain.employeecard.EmployeeCard;

public record EmployeeCardResponseDTO(
        Long id,
        String cardNumber
) {
    public static EmployeeCardResponseDTO fromEntity(EmployeeCard e) {
        return new EmployeeCardResponseDTO(e.getId(), e.getCardNumber());
    }
}
