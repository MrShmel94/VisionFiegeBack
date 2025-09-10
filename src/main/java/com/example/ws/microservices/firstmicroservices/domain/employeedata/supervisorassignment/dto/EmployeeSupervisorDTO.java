package com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSupervisorDTO implements Serializable {
    private Long employeeId;
    private String supervisorExpertis;
}