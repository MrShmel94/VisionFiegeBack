package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisorId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeSupervisorRepository extends JpaRepository<EmployeeSupervisor, EmployeeSupervisorId> {

    @Query("SELECT es FROM EmployeeSupervisor es WHERE es.validTo > :currentDate")
    List<EmployeeSupervisor> findActiveAccesses(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT es FROM EmployeeSupervisor es WHERE es.validTo < :currentDate")
    List<EmployeeSupervisor> findExpiredAccesses(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT es FROM EmployeeSupervisor es WHERE es.employeeId = :employeeId")
    List<EmployeeSupervisor> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Modifying
    @Transactional
    @Query(value = """
    WITH input_data AS (
      SELECT 
        unnest(?::varchar[]) AS expertis,
        unnest(?::timestamp[]) AS valid_from,
        unnest(?::timestamp[]) AS valid_to
    ),
    supervisor AS (
      SELECT id AS supervisor_id
      FROM employee
      WHERE id = ?
    ),
    rows_to_insert AS (
      SELECT e.id AS employee_id,
             e.expertis,
             s.supervisor_id,
             COALESCE(i.valid_from, NOW()) AS valid_from,
             COALESCE(i.valid_to, '9999-12-31 23:59:59'::timestamp) AS valid_to
      FROM input_data i
      JOIN employee e ON e.expertis = i.expertis
      CROSS JOIN supervisor s
    ),
    inserted AS (
      INSERT INTO employee_supervisors (employee_id, supervisor_id, valid_from, valid_to)
      SELECT employee_id, supervisor_id, valid_from, valid_to
      FROM rows_to_insert
      ON CONFLICT (employee_id) DO NOTHING
      RETURNING employee_id
    )
    SELECT r.expertis
    FROM rows_to_insert r
    JOIN inserted i ON r.employee_id = i.employee_id
    """,
            nativeQuery = true)
    List<String> insertEmployeeSupervisors(
            String[] expertis,
            LocalDateTime[] validFrom,
            LocalDateTime[] validTo,
            Long supervisorId
    );
}
