package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployeeWithoutNttTatig;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ClearPerformanceEmployeeWithoutNttService {
    void saveAllPerformanceEmployee(List<ClearPerformanceEmployeeWithoutNttTatig> employeeList);
    Map<String, List<ClearPerformanceGDDto>> getAllPerformanceEmployee(LocalDate start, LocalDate end);
}
