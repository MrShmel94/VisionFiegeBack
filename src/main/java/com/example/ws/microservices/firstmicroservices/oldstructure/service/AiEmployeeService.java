package com.example.ws.microservices.firstmicroservices.oldstructure.service;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.aiemployee.AiEmployee;

import java.util.List;

public interface AiEmployeeService {
     void saveAiEmployee(List<AiEmployee> aiEmployee);
     AiEmployee getAiEmployeeById(Long id);
     void save(AiEmployee aiEmployee);
}
