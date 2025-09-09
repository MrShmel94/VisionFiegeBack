package com.example.ws.microservices.firstmicroservices.oldstructure.request.websocket;

public record SelectedEmployeeRequest(
        Long id,
        String firstName,
        String lastName,
        String expertis,
        String department,
        String team,
        String position,
        String siteName
) {
}
