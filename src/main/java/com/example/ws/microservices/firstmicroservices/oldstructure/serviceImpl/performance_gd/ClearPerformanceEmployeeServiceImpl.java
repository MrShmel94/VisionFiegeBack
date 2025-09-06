package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployee;
import com.example.ws.microservices.firstmicroservices.oldstructure.mapper.ClearPerformanceMapper;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.ClearPerformanceEmployeeRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.ClearPerformanceEmployeeService;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.GraphLayout;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClearPerformanceEmployeeServiceImpl implements ClearPerformanceEmployeeService {

    private final ClearPerformanceEmployeeRepository clearPerformanceEmployeeRepository;
    private final RedisService redisService;

    @Override
    public void saveAllPerformanceEmployee(List<ClearPerformanceEmployee> clearPerformanceEmployees) {
        clearPerformanceEmployeeRepository.saveAll(clearPerformanceEmployees);
    }

    @Override
    public Map<String, ClearPerformanceGDDto> getAllPerformanceEmployee(LocalDate start, LocalDate end) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeUsedMem = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();

        Map<String, ClearPerformanceGDDto> result = new HashMap<>();

        List<LocalDate> allDates = start.datesUntil(end.plusDays(1)).toList();
        List<LocalDate> missingDates = new ArrayList<>();

        for (LocalDate date : allDates) {
            String redisKey = String.format("clearPerformance:%s", date);
            Map<String, ClearPerformanceGDDto> cached = redisService.getAllFromHash(
                    redisKey, new TypeReference<ClearPerformanceGDDto>() {}
            );

            if (!cached.isEmpty()) {
                mergeResult(result, cached);
            } else {
                missingDates.add(date);
            }
        }

        if (!missingDates.isEmpty()) {
            List<ClearPerformanceGDDto> fromDb = clearPerformanceEmployeeRepository
                    .findAllByDateIn(missingDates)
                    .stream()
                    .map(ClearPerformanceMapper.INSTANCE::toClearPerformanceGDD)
                    .toList();

            Map<LocalDate, List<ClearPerformanceGDDto>> groupedByDate = fromDb.stream()
                    .collect(Collectors.groupingBy(ClearPerformanceGDDto::getDate));

            for (Map.Entry<LocalDate, List<ClearPerformanceGDDto>> entry : groupedByDate.entrySet()) {
                String redisKey = String.format("clearPerformance:%s", entry.getKey());
                Map<String, ClearPerformanceGDDto> groupedByExpertis = entry.getValue().stream()
                        .collect(Collectors.toMap(ClearPerformanceGDDto::getExpertis, Function.identity()));
                redisService.saveAllMapping(redisKey, groupedByExpertis);
                mergeResult(result, groupedByExpertis);
            }
        }

        System.out.println(GraphLayout.parseInstance(result).toFootprint());

        long endTime = System.nanoTime();
        System.out.printf("Execution time: %.2f ms%n", (endTime - startTime) / 1_000_000.0);
        long afterUsedMem = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Memory used: %.2f MB%n", (afterUsedMem - beforeUsedMem) / 1024.0 / 1024.0);

        return result;
    }

    private void mergeResult(Map<String, ClearPerformanceGDDto> result,
                             Map<String, ClearPerformanceGDDto> newData) {
        for (Map.Entry<String, ClearPerformanceGDDto> entry : newData.entrySet()) {
            result.merge(entry.getKey(), entry.getValue(), ClearPerformanceGDDto::mergeWith);
        }
    }
}
