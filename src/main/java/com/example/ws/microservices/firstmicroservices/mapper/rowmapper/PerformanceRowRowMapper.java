package com.example.ws.microservices.firstmicroservices.mapper.rowmapper;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.PerformanceRowDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PerformanceRowRowMapper implements RowMapper<PerformanceRowDTO> {

    @Override
    public PerformanceRowDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PerformanceRowDTO.builder()
                .date(rs.getDate("date").toLocalDate())
                .expertis(rs.getString("expertis"))
                .activityName(rs.getString("activity_name"))
                .finalCluster(rs.getString("final_cluster"))
                .activityCluster(rs.getString("activity_cluster"))
                .startActivity(rs.getTimestamp("start_activity").toLocalDateTime())
                .endActivity(rs.getTimestamp("end_activity").toLocalDateTime())
                .duration(rs.getDouble("duration"))
                .ql(rs.getInt("ql"))
                .qlBox(rs.getInt("ql_box"))
                .qlHanging(rs.getInt("ql_hanging"))
                .qlShoes(rs.getInt("ql_shoes"))
                .qlBoots(rs.getInt("ql_boots"))
                .qlOther(rs.getInt("ql_other"))
                .stowClarifications(rs.getInt("stow_clarifications"))
                .pickNos1(rs.getInt("pick_nos1"))
                .pickNos2(rs.getInt("pick_nos2"))
                .build();
    }
}
