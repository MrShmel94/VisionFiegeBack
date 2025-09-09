package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ClearPerformanceEmployeeService {
    void saveAllPerformanceEmployee(List<ClearPerformanceEmployee> employeeList);
    Map<String, ClearPerformanceGDDto> getAllPerformanceEmployee(LocalDate start, LocalDate end);
}
