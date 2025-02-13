package com.example.ws.microservices.firstmicroservices.serviceImpl;


import com.example.ws.microservices.firstmicroservices.dto.PerformanceDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ClickHousePerformanceServiceImpl {

    private final JdbcTemplate clickHouseJdbcTemplate;

    public void savePerformances(List<PerformanceDTO> performances) {
        String sql = """
            INSERT INTO performance (
                date, expertis, activity_name, spi_cluster, final_cluster,
                start_activity, end_activity, duration, ql, ql_box, ql_hanging,
                ql_shoes, ql_boots, ql_other, stow_clarifications,
                pick_nos1, pick_nos2
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        List<Object[]> batch = performances.stream()
                .map(performance -> new Object[]{
                        performance.getDate(),
                        performance.getExpertis(),
                        performance.getActivityName(),
                        performance.getSpiCluster(),
                        performance.getFinalCluster(),
                        performance.getStartActivity(),
                        performance.getEndActivity(),
                        performance.getDuration(),
                        performance.getQl(),
                        performance.getQlBox(),
                        performance.getQlHanging(),
                        performance.getQlShoes(),
                        performance.getQlBoots(),
                        performance.getQlOther(),
                        performance.getStowClarifications(),
                        performance.getPickNos1(),
                        performance.getPickNos2()
                })
                .toList();

        clickHouseJdbcTemplate.batchUpdate(sql, batch);
        log.info("Saved {} records to ClickHouse", performances.size());
    }

}
