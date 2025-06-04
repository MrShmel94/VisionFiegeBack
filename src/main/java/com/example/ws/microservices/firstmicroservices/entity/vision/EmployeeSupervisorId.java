package com.example.ws.microservices.firstmicroservices.entity.vision;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSupervisorId implements Serializable {
    private Long employeeId;
    private String supervisorExpertis;
}