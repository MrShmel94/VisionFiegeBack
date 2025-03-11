package com.example.ws.microservices.firstmicroservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PreviewEmployeeDTO {

    private String expertis;
    private String firstName;
    private String lastName;
    private String department;
    private String team;

}
