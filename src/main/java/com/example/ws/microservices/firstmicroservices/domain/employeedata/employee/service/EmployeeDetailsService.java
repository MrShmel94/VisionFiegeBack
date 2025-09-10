package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.service;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;

import java.util.List;

public interface EmployeeDetailsService {
     void saveAiEmployee(List<EmployeeDetails> employeeDetails);
     EmployeeDetails getAiEmployeeById(Long id);
     void save(EmployeeDetails employeeDetails);
}
