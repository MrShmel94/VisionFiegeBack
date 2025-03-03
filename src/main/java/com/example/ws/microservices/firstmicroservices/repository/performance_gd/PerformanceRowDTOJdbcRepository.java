package com.example.ws.microservices.firstmicroservices.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.PerformanceRowDTO;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.PerformanceRow;
import com.example.ws.microservices.firstmicroservices.mapper.rowmapper.PerformanceRowRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PerformanceRowDTOJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<PerformanceRowDTO> getAllByDateBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT p.date, " +
                "       p.expertis, " +
                "       an.name as activity_name, " +
                "       fc.name as final_cluster, " +
                "       sc.name as activity_cluster, " +
                "       p.start_activity, " +
                "       p.end_activity, " +
                "       p.duration, " +
                "       p.ql, " +
                "       p.ql_box, " +
                "       p.ql_hanging, " +
                "       p.ql_shoes, " +
                "       p.ql_boots, " +
                "       p.ql_other, " +
                "       p.stow_clarifications, " +
                "       p.pick_nos1, " +
                "       p.pick_nos2 " +
                "FROM performance_dg.performance p " +
                "LEFT JOIN performance_dg.activity_name an ON p.activity_name_id = an.id " +
                "LEFT JOIN performance_dg.final_cluster fc ON p.final_cluster_id = fc.id " +
                "LEFT JOIN performance_dg.spi_cluster sc ON p.activity_cluster_id = sc.id " +
                "WHERE p.date BETWEEN ? AND ?";

        return jdbcTemplate.query(sql,
                new PerformanceRowRowMapper(),
                Date.valueOf(start),
                Date.valueOf(end));
    }
}
