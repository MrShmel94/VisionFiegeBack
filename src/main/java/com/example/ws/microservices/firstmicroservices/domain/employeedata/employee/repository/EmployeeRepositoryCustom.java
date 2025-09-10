package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeMapping;

import java.util.List;

public interface EmployeeRepositoryCustom {
    void saveEmployeesAndAiData(List<EmployeeMapping> employees, List<EmployeeDetails> employeeDetails, int chunkSize);
}
