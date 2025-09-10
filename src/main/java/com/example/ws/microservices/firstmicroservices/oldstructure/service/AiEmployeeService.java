package com.example.ws.microservices.firstmicroservices.oldstructure.service;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;

import java.util.List;

public interface AiEmployeeService {
     void saveAiEmployee(List<EmployeeDetails> employeeDetails);
     EmployeeDetails getAiEmployeeById(Long id);
     void save(EmployeeDetails employeeDetails);
}
