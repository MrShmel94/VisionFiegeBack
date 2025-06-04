package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.vision.AiEmployee;
import com.example.ws.microservices.firstmicroservices.entity.vision.EmployeeMapping;

import java.util.List;

public interface EmployeeRepositoryCustom {
    void saveEmployeesAndAiData(List<EmployeeMapping> employees, List<AiEmployee> aiEmployees, int chunkSize);
}
