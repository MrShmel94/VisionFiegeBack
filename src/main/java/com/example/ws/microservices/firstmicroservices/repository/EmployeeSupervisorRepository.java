package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.entity.vision.EmployeeSupervisorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeSupervisorRepository extends JpaRepository<EmployeeSupervisor, EmployeeSupervisorId> {

    @Query(value = """
            WITH deleted AS (
                DELETE FROM vision.employee_supervisors
                WHERE valid_to < now()
                RETURNING employee_id, supervisor_id
            )
            SELECT sup.expertis AS supervisor_expertis,
                   emp.expertis AS employee_expertis,
                   emp.id AS employee_id
            FROM deleted d
            JOIN vision.employee emp ON d.employee_id = emp.id
            JOIN vision.employee sup ON d.supervisor_id = sup.id;
            """, nativeQuery = true)
    List<Object[]> findExpiredAccessesAndDeleteThem();

    @Modifying
    @Query(value = """
    WITH input_data AS (
      SELECT
        unnest(?::varchar[]) AS expertis,
        unnest(?::date[]) AS valid_from,
        unnest(?::date[]) AS valid_to
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
             COALESCE(i.valid_to, '9999-12-31'::DATE) AS valid_to,
             ?::varchar AS user_id
      FROM input_data i
      JOIN vision.employee e ON e.expertis = i.expertis
      CROSS JOIN supervisor s
    ),
    inserted AS (
      INSERT INTO vision.employee_supervisors (employee_id, supervisor_id, valid_from, valid_to, user_id)
      SELECT employee_id, supervisor_id, valid_from, valid_to, user_id
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
            LocalDate[] validFrom,
            LocalDate[] validTo,
            Long supervisorId,
            String userId
    );

    @Modifying
    @Query(value = """
    WITH input_data AS (
      SELECT unnest(?::varchar[]) AS employee_expertis
    ),
    supervisor AS (
      SELECT id AS supervisor_id
      FROM vision.employee
      WHERE expertis = ?
    ),
    employees_to_delete AS (
      SELECT e.id AS employee_id,
             e.expertis AS employee_expertis,
             s.supervisor_id
      FROM input_data i
      JOIN vision.employee e ON e.expertis = i.employee_expertis
      CROSS JOIN supervisor s
    )
    DELETE FROM vision.employee_supervisors es
    USING employees_to_delete etd
    WHERE es.employee_id = etd.employee_id
      AND es.supervisor_id = etd.supervisor_id
    RETURNING etd.employee_expertis;
    """, nativeQuery = true)
    List<String> deleteEmployeesForSupervisor(
            String[] employeeExpertis,
            String supervisorExpertis
    );

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO(
            e.id, e.expertis, e.brCode, e.firstName, e.lastName, e.sex,
            t.name, s.name, sh.name, c.name, d.name, p.name, e.isSupervisor, e.isCanHasAccount, e.validToAccount, e.validFromAccount, a.name, ai.note, ai.dateStartContract, ai.dateFinishContract,
            ai.dateBhpNow, ai.dateBhpFuture, ai.dateAdrNow, ai.dateAdrFuture, ai.fte, COALESCE(CONCAT(em.firstName, ' ', em.lastName), 'No Supervisor'), COALESCE(em.expertis, 'No Supervisor')
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

    @Query(value = """
            SELECT e.expertis
            FROM vision.employee e
            JOIN vision.employee_supervisors es ON es.employee_id = e.id
            JOIN vision.employee em ON em.id = es.supervisor_id
            WHERE em.expertis = :expertis
            """, nativeQuery = true)
    List<String> getAllExpertisEmployeeBySupervisor(@Param("expertis") String expertis);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO(
           e.id, e.expertis, e.firstName, e.lastName, d.name, t.name, p.name, s.name
           ) FROM Employee e
           JOIN Department d ON e.departmentId = d.id
           JOIN Team t ON e.teamId = t.id
           JOIN Position p ON e.positionId = p.id
           JOIN Site s ON e.siteId = s.id
           WHERE e.isWork = true
           AND s.name = :siteName
           AND e.id NOT IN (SELECT es.employeeId FROM EmployeeSupervisor es)
           """)
    List<PreviewEmployeeDTO> getEmployeeWithoutSupervisor(@Param("siteName") String siteName);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO(
           e.id, e.expertis, e.firstName, e.lastName, d.name, t.name, p.name, s.name
           ) FROM Employee e
           JOIN Department d ON e.departmentId = d.id
           JOIN Team t ON e.teamId = t.id
           JOIN Position p ON e.positionId = p.id
           JOIN Site s ON e.siteId = s.id
           WHERE e.isWork = true
           AND s.name = :siteName
           AND e.isSupervisor = true
           """)
    List<PreviewEmployeeDTO> getSupervisors(@Param("siteName") String siteName);
}
