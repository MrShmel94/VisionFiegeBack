package com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.dto;

import app.domain.employeecard.EmployeeCard;

public record EmployeeCardResponseDTO(
        Long id,
        String cardNumber
) {
    public static EmployeeCardResponseDTO fromEntity(EmployeeCard e) {
        return new EmployeeCardResponseDTO(e.getId(), e.getCardNumber());
    }
}
