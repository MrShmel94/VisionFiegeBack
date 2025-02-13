package com.example.ws.microservices.firstmicroservices.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.CheckHeader;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PerformanceService {
    void processFile(List<List<String>> allLineFiles, Map<String, String> checkHeaderList, List<String> tablesNameInIndex);
    void processFileClickHouse(List<List<String>> allLineFiles, Map<String, String> checkHeaderList, List<String> tablesNameInIndex);
}
