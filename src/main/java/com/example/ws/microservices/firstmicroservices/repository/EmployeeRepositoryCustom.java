package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.aiemployee.AiEmployee;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.EmployeeMapping;

import java.util.List;

public interface EmployeeRepositoryCustom {
    void saveEmployeesAndAiData(List<EmployeeMapping> employees, List<AiEmployee> aiEmployees, int chunkSize);
}
