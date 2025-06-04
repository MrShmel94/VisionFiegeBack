package com.example.ws.microservices.firstmicroservices.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.ClearPerformanceEmployee;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.ClearPerformanceEmployeeWithoutNttTatig;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ClearPerformanceEmployeeWithoutNttService {
    void saveAllPerformanceEmployee(List<ClearPerformanceEmployeeWithoutNttTatig> employeeList);
    Map<String, List<ClearPerformanceGDDto>> getAllPerformanceEmployee(LocalDate start, LocalDate end);
}
