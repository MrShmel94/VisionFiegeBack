package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface PerformanceService {
    void processFile(Connection conn, List<List<String>> allLineFiles, Map<String, String> checkHeaderList, List<String> tablesNameInIndex) throws SQLException;
}
