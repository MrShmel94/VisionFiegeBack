package com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.dto;

import com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.EmployeeCard;


public record EmployeeCardResponseDTO(
        Long id,
        String cardNumber
) {
    public static EmployeeCardResponseDTO fromEntity(EmployeeCard e) {
        return new EmployeeCardResponseDTO(e.getId(), e.getCardNumber());
    }
}
