package com.example.ws.microservices.firstmicroservices.request.websocket;

public record SelectedEmployeeRequest(
        Long id,
        String firstName,
        String lastName,
        String expertis,
        String department,
        String team,
        String position
) {
}
