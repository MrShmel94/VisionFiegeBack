package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto;

public record UserMeDTO(
        String firstName,
        String lastName,
        String department,
        String position
) {
}
