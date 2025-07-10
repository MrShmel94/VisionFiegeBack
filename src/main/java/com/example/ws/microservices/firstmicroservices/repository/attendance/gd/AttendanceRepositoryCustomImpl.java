package com.example.ws.microservices.firstmicroservices.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.AttendanceChangeRequest;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
@AllArgsConstructor
public class AttendanceRepositoryCustomImpl implements AttendanceRepositoryCustom{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkUpsert(List<Attendance> attendances) {
        String sql = "INSERT INTO attendance.attendance " +
                "(employee_id, date, shift_id, site_id, status_id, department_id, hours_worked, comment, user_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (employee_id, date, site_id) " +
                "DO NOTHING " ;

        batchUpdate(attendances, sql);
    }

    @Override
    public void createPartitionIfNotExists(LocalDate targetDate) {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {

            String currentSchema = connection.getSchema();
            String desiredSchema = "attendance";

            if (!desiredSchema.equalsIgnoreCase(currentSchema)) {
                connection.setSchema(desiredSchema);
            }

            try (CallableStatement cs = connection.prepareCall("{ call attendance.create_monthly_partition(?) }")) {
                cs.setDate(1, Date.valueOf(targetDate));
                cs.execute();
            }
            return null;
        });
    }

    @Override
    public void bulkUpdate(List<Attendance> attendances) {
        String sql = """
                INSERT INTO attendance.attendance
                (employee_id, date, shift_id, site_id, status_id, department_id, hours_worked, comment, user_id, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (employee_id, date, site_id)
                DO UPDATE SET
                    shift_id = EXCLUDED.shift_id,
                    status_id = EXCLUDED.status_id,
                    department_id = EXCLUDED.department_id,
                    site_id = EXCLUDED.site_id,
                    hours_worked = EXCLUDED.hours_worked,
                    comment = EXCLUDED.comment,
                    user_id = EXCLUDED.user_id,
                    created_at = now();
                """;

        batchUpdate(attendances, sql);
    }

    private void batchUpdate(List<Attendance> attendances, String sql) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Attendance a = attendances.get(i);
                ps.setLong(1, a.getEmployee().getId());
                ps.setDate(2, java.sql.Date.valueOf(a.getDate()));
                ps.setInt(3, a.getShift().getId());
                ps.setInt(4, a.getSite().getId());
                ps.setInt(5, a.getStatus().getId());
                ps.setInt(6, a.getDepartment().getId());
                ps.setDouble(7, a.getHoursWorked() != null ? a.getHoursWorked() : 0);
                ps.setString(8, a.getComment());
                ps.setString(9, a.getUserId());
                ps.setTimestamp(10, Timestamp.from(a.getCreatedAt() != null ? a.getCreatedAt() : Instant.now()));
            }
            @Override
            public int getBatchSize() {
                return attendances.size();
            }
        });
    }
}
