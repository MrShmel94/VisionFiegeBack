package com.example.ws.microservices.firstmicroservices.repository.etc.planed;

import com.example.ws.microservices.firstmicroservices.entity.etc.planed.PlanedEmployee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@AllArgsConstructor
public class PlanedRepositoryCustomImpl implements PlanedRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void savePlanedEmployeeTraining(List<PlanedEmployee> planedEmployees) {

        String sql =
        """
        INSERT INTO etc.planed_employee
        (employee_id, planed_training_id, is_present, date, user_id)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (employee_id, planed_training_id) DO NOTHING
        """;

        batchUpdate(planedEmployees, sql);
    }

    private void batchUpdate(List<PlanedEmployee> planedEmployees, String sql) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PlanedEmployee pe = planedEmployees.get(i);
                ps.setLong(1, pe.getEmployee().getId());
                ps.setLong(2, pe.getPlanedTraining().getId());
                ps.setBoolean(3, pe.getIsPresent());
                ps.setTimestamp(4, Timestamp.from(pe.getDate()));
                ps.setString(5, pe.getUserId());
            }

            @Override
            public int getBatchSize() {
                return planedEmployees.size();
            }
        });
    }

    @Override
    public void deletePlanedEmployeeTraining(List<PlanedEmployee> planedEmployees, String userId) {

        setCurrentUserId(userId);

        String sql = """
        DELETE FROM etc.planed_employee
        WHERE employee_id = ? AND planed_training_id = ?
    """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PlanedEmployee pe = planedEmployees.get(i);
                ps.setLong(1, pe.getEmployee().getId());
                ps.setLong(2, pe.getPlanedTraining().getId());
            }

            @Override
            public int getBatchSize() {
                return planedEmployees.size();
            }
        });
    }

    private void setCurrentUserId(String userId) {
        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, true)")
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
