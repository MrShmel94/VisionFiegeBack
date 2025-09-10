package com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SupervisorFullNameDTO {
    private String firstName;
    private String lastName;
}
