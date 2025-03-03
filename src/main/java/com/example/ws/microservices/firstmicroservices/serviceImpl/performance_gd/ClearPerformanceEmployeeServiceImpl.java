package com.example.ws.microservices.firstmicroservices.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.ClearPerformanceEmployee;
import com.example.ws.microservices.firstmicroservices.repository.performance_gd.ClearPerformanceEmployeeRepository;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.ClearPerformanceEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClearPerformanceEmployeeServiceImpl implements ClearPerformanceEmployeeService {

    private final ClearPerformanceEmployeeRepository clearPerformanceEmployeeRepository;

    @Override
    public void saveAllPerformanceEmployee(List<ClearPerformanceEmployee> clearPerformanceEmployees) {
        clearPerformanceEmployeeRepository.saveAll(clearPerformanceEmployees);
    }
}
