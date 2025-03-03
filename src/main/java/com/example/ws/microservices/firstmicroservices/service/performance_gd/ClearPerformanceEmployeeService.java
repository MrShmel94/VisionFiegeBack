package com.example.ws.microservices.firstmicroservices.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.ClearPerformanceEmployee;

import java.util.List;

public interface ClearPerformanceEmployeeService {
    void saveAllPerformanceEmployee(List<ClearPerformanceEmployee> employeeList);
}
