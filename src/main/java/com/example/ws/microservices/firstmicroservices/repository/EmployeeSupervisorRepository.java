package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
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
      FROM vision.employee
      WHERE id = ?
    ),
    rows_to_insert AS (
      SELECT e.id AS employee_id,
             e.expertis,
             s.supervisor_id,
             COALESCE(i.valid_from, NOW()) AS valid_from,
             COALESCE(i.valid_to, '9999-12-31 23:59:59'::timestamp) AS valid_to
      FROM input_data i
      JOIN vision.employee e ON e.expertis = i.expertis
      CROSS JOIN supervisor s
    ),
    inserted AS (
      INSERT INTO vision.employee_supervisors (employee_id, supervisor_id, valid_from, valid_to)
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

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO(
            e.id, e.expertis, e.zalosId, e.brCode, e.firstName, e.lastName, e.sex,
            t.name, s.name, sh.name, c.name, d.name, p.name, e.isSupervisor, e.isCanHasAccount, e.validToAccount, e.validFromAccount, a.name, ai.note, ai.dateStartContract, ai.dateFinishContract,
            ai.dateBhpNow, ai.dateBhpFuture, ai.dateAdrNow, ai.dateAdrFuture, ai.fte
            )FROM Employee e
            JOIN Site s ON e.siteId = s.id
            JOIN Shift sh ON e.shiftId = sh.id
            JOIN Department d ON e.departmentId = d.id
            JOIN Team t ON e.teamId = t.id
            JOIN Position p ON e.positionId = p.id
            JOIN Agency a ON e.agencyId = a.id
            JOIN Country c ON e.countryId = c.id
            JOIN AiEmployee ai ON e.id = ai.employee.id
            JOIN EmployeeSupervisor es ON es.employeeId = e.id
            JOIN Employee em ON es.supervisorId = em.id
            WHERE em.expertis = :expertis
            """)
    List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor(@Param("expertis") String expertis);
}
