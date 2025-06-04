package com.example.ws.microservices.firstmicroservices.dto;

public record UserMeDTO(
        String firstName,
        String lastName,
        String department,
        String position
) {
}
