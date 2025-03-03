package com.example.ws.microservices.firstmicroservices.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.CheckHeader;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.PerformanceRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PerformanceService {
    void processFile(Connection conn, List<List<String>> allLineFiles, Map<String, String> checkHeaderList, List<String> tablesNameInIndex);
}
