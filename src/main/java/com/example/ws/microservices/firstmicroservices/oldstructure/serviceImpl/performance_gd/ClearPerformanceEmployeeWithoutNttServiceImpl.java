package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployeeWithoutNttTatig;
import com.example.ws.microservices.firstmicroservices.oldstructure.mapper.ClearPerformanceWithoutNttMapper;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.ClearPerformanceEmployeeWithoutNttRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.ClearPerformanceEmployeeWithoutNttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClearPerformanceEmployeeWithoutNttServiceImpl implements ClearPerformanceEmployeeWithoutNttService {

    private final ClearPerformanceEmployeeWithoutNttRepository clearPerformanceEmployeeWithoutNttRepository;

    @Override
    public void saveAllPerformanceEmployee(List<ClearPerformanceEmployeeWithoutNttTatig> clearPerformanceEmployees) {
        clearPerformanceEmployeeWithoutNttRepository.saveAll(clearPerformanceEmployees);
    }

    @Override
    public Map<String, List<ClearPerformanceGDDto>> getAllPerformanceEmployee(LocalDate start, LocalDate end) {
        List<ClearPerformanceGDDto> clearPerformance = clearPerformanceEmployeeWithoutNttRepository.findAllByDateBetween(start, end).stream().map(ClearPerformanceWithoutNttMapper.INSTANCE::toClearPerformanceGDD).toList();
        return clearPerformance.stream().collect(Collectors.groupingBy(ClearPerformanceGDDto::getExpertis));
    }
}
