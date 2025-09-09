package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestSetEmployeeToSupervisor {
    private String supervisorExpertis;
    private Map<String, DateRequest> requests;
}
