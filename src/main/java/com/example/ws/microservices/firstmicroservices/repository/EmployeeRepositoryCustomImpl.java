package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.aiemployee.AiEmployee;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.EmployeeMapping;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private final EntityManager entityManager;

    private static final String SQL_INSERT_AI_EMPLOYEE = """
        INSERT INTO vision.ai_employee (
            note, date_start_contract, date_finish_contract,
            date_bhp_now, date_bhp_future, date_adr_now, date_adr_future,
            fte, employee_id, user_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    @Override
    @Transactional
    public void saveEmployeesAndAiData(List<EmployeeMapping> employees, List<AiEmployee> aiEmployees, int chunkSize) {
        for (int i = 0; i < employees.size(); i++) {
            entityManager.persist(employees.get(i));
            if (i > 0 && i % chunkSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();

        for (int i = 0; i < aiEmployees.size(); i++) {
            AiEmployee ai = aiEmployees.get(i);
            EmployeeMapping employee = employees.get(i);
            ai.setEmployee(employee);
        }

        jdbcTemplate.batchUpdate(SQL_INSERT_AI_EMPLOYEE, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AiEmployee ai = aiEmployees.get(i);
                ps.setString(1, ai.getNote());
                ps.setDate(2, Date.valueOf(ai.getDateStartContract()));
                ps.setDate(3, Date.valueOf(ai.getDateFinishContract()));
                ps.setDate(4, ai.getDateBhpNow() != null ? Date.valueOf(ai.getDateBhpNow()) : null);
                ps.setDate(5, ai.getDateBhpFuture() != null ? Date.valueOf(ai.getDateBhpFuture()) : null);
                ps.setDate(6, ai.getDateAdrNow() != null ? Date.valueOf(ai.getDateAdrNow()) : null);
                ps.setDate(7, ai.getDateAdrFuture() != null ? Date.valueOf(ai.getDateAdrFuture()) : null);
                ps.setDouble(8, ai.getFte());
                ps.setLong(9, ai.getEmployee().getId());
                ps.setString(10, ai.getUserId());
            }

            @Override
            public int getBatchSize() {
                return aiEmployees.size();
            }
        });
    }
}
