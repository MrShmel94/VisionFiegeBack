package com.example.ws.microservices.firstmicroservices.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@AllArgsConstructor
public class AttendanceRepositoryCustomImpl implements AttendanceRepositoryCustom{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkUpsert(List<Attendance> attendances) {
        String sql = "INSERT INTO attendance_gd.attendance " +
                "(employee_id, date, shift_id, status_id, hours_worked, comment, user_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (employee_id, date) " +
                "DO UPDATE SET " +
                "shift_id = EXCLUDED.shift_id, " +
                "status_id = EXCLUDED.status_id, " +
                "hours_worked = EXCLUDED.hours_worked, " +
                "comment = EXCLUDED.comment, " +
                "user_id = EXCLUDED.user_id, " +
                "created_at = EXCLUDED.created_at";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Attendance a = attendances.get(i);
                ps.setLong(1, a.getEmployee().getId());
                ps.setDate(2, java.sql.Date.valueOf(a.getDate()));
                ps.setInt(3, a.getShift().getId());
                ps.setInt(4, a.getStatus().getId());
                ps.setDouble(5, a.getHoursWorked() != null ? a.getHoursWorked() : 0);
                ps.setString(6, a.getComment());
                ps.setString(7, a.getUserId());
                ps.setTimestamp(8, Timestamp.from(a.getCreatedAt() != null ? a.getCreatedAt() : Instant.now()));
            }
            @Override
            public int getBatchSize() {
                return attendances.size();
            }
        });
    }
}
