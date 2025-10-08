package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.InformationContractDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeDTO("
            + "e.id, e.expertis, e.brCode, "
            + "e.firstName, e.lastName, e.isWork, e.sex, "
            + "s.name, sh.name, d.name, t.name, p.name, a.name, e.isCanHasAccount, e.isSupervisor, e.validToAccount, e.validFromAccount, st.name, e.temporaryAssignmentFrom, e.temporaryAssignmentTo) "
            + "FROM Employee e "
            + "JOIN Site s ON e.siteId = s.id "
            + "LEFT JOIN Site st ON e.temporaryAssignmentSiteId = st.id "
            + "JOIN Shift sh ON e.shiftId = sh.id "
            + "JOIN Department d ON e.departmentId = d.id "
            + "JOIN Team t ON e.teamId = t.id "
            + "JOIN Position p ON e.positionId = p.id "
            + "JOIN Agency a ON e.agencyId = a.id "
            + "WHERE e.expertis = :expertis")
    Optional<EmployeeDTO> findEmployeeByExpertis(@Param("expertis") String expertis);

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO(
            e.id, e.expertis, e.brCode, e.firstName, e.lastName, e.sex,
            t.name, s.name, sh.name, c.name, d.name, p.name, e.isSupervisor, e.isCanHasAccount, e.validToAccount, e.validFromAccount, a.name, ai.note, ai.dateStartContract, ai.dateFinishContract,
            ai.dateBhpNow, ai.dateBhpFuture, ai.dateAdrNow, ai.dateAdrFuture, ai.fte, COALESCE(CONCAT(supervisor.firstName, ' ', supervisor.lastName), 'No Supervisor'), COALESCE(supervisor.expertis, 'No Supervisor'), st.name, e.temporaryAssignmentFrom, e.temporaryAssignmentTo
            )FROM Employee e
            JOIN Site s ON e.siteId = s.id
            JOIN Site st ON e.temporaryAssignmentSiteId = st.id
            JOIN Shift sh ON e.shiftId = sh.id
            JOIN Department d ON e.departmentId = d.id
            JOIN Team t ON e.teamId = t.id
            JOIN Position p ON e.positionId = p.id
            JOIN Agency a ON e.agencyId = a.id
            JOIN Country c ON e.countryId = c.id
            JOIN EmployeeDetails ai ON e.id = ai.employee.id
            LEFT JOIN SupervisorAssignment es ON es.employeeId = e.id
            LEFT JOIN Employee supervisor ON es.supervisorId = supervisor.id
            WHERE e.expertis IN :expertisList
            """)
    Page<EmployeeFullInformationDTO> findEmployeeFullInformationByExpertisListPageable(@Param("expertisList") List<String> expertisList, Pageable pageable);

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO(
            e.id, e.expertis, e.brCode, e.firstName, e.lastName, e.sex,
            t.name, s.name, sh.name, c.name, d.name, p.name, e.isSupervisor, e.isCanHasAccount, e.validToAccount, e.validFromAccount, a.name, ai.note, ai.dateStartContract, ai.dateFinishContract,
            ai.dateBhpNow, ai.dateBhpFuture, ai.dateAdrNow, ai.dateAdrFuture, ai.fte, COALESCE(CONCAT(supervisor.firstName, ' ', supervisor.lastName), 'No Supervisor'), COALESCE(supervisor.expertis, 'No Supervisor'), st.name, e.temporaryAssignmentFrom, e.temporaryAssignmentTo
            )FROM Employee e
            JOIN Site s ON e.siteId = s.id
            JOIN Site st ON e.temporaryAssignmentSiteId = st.id
            JOIN Shift sh ON e.shiftId = sh.id
            JOIN Department d ON e.departmentId = d.id
            JOIN Team t ON e.teamId = t.id
            JOIN Position p ON e.positionId = p.id
            JOIN Agency a ON e.agencyId = a.id
            JOIN Country c ON e.countryId = c.id
            JOIN EmployeeDetails ai ON e.id = ai.employee.id
            LEFT JOIN SupervisorAssignment es ON es.employeeId = e.id
            LEFT JOIN Employee supervisor ON es.supervisorId = supervisor.id
            WHERE e.expertis IN :expertisList
            """)
    List<EmployeeFullInformationDTO> findEmployeeFullInformationByExpertisList(@Param("expertisList") List<String> expertisList);

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO(
            e.id, e.expertis, e.brCode, e.firstName, e.lastName, e.sex,
            t.name, s.name, sh.name, c.name, d.name, p.name, e.isSupervisor, e.isCanHasAccount, e.validToAccount, e.validFromAccount, a.name, ai.note, ai.dateStartContract, ai.dateFinishContract,
            ai.dateBhpNow, ai.dateBhpFuture, ai.dateAdrNow, ai.dateAdrFuture, ai.fte, COALESCE(CONCAT(supervisor.firstName, ' ', supervisor.lastName), 'No Supervisor'), COALESCE(supervisor.expertis, 'No Supervisor'), st.name, e.temporaryAssignmentFrom, e.temporaryAssignmentTo
            )FROM Employee e
            JOIN Site s ON e.siteId = s.id
            JOIN Site st ON e.temporaryAssignmentSiteId = st.id
            JOIN Shift sh ON e.shiftId = sh.id
            JOIN Department d ON e.departmentId = d.id
            JOIN Team t ON e.teamId = t.id
            JOIN Position p ON e.positionId = p.id
            JOIN Agency a ON e.agencyId = a.id
            JOIN Country c ON e.countryId = c.id
            JOIN EmployeeDetails ai ON e.id = ai.employee.id
            LEFT JOIN SupervisorAssignment es ON es.employeeId = e.id
            LEFT JOIN Employee supervisor ON es.supervisorId = supervisor.id
            WHERE e.expertis = :expertis
            """)
    Optional<EmployeeFullInformationDTO> findEmployeeFullInformationByExpertis(@Param("expertis") String expertis);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.InformationContractDTO(
           e.expertis, ae.dateStartContract, ae.dateFinishContract
           ) FROM Employee e
           LEFT JOIN EmployeeDetails ae ON ae.id = e.id
           WHERE e.expertis IN :expertisList
           """)
    List<InformationContractDTO> getAllInformationContractsByListExertis(@Param("expertisList") List<String> listExertis);

    @Query(value = """
           SELECT expertis
           FROM vision.employee
           WHERE is_work = true
           """, nativeQuery = true)
    List<String> getAllExpertis();

    Optional<Employee> findByExpertis(String expertis);

    @Query("""
    SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO(
            e.id, e.expertis, e.brCode, e.firstName, e.lastName, e.sex,
            t.name, s.name, sh.name, c.name, d.name, p.name, e.isSupervisor, e.isCanHasAccount, e.validToAccount, e.validFromAccount, a.name, ai.note, ai.dateStartContract, ai.dateFinishContract,
            ai.dateBhpNow, ai.dateBhpFuture, ai.dateAdrNow, ai.dateAdrFuture, ai.fte, COALESCE(CONCAT(supervisor.firstName, ' ', supervisor.lastName), 'No Supervisor'), COALESCE(supervisor.expertis, 'No Supervisor'), st.name, e.temporaryAssignmentFrom, e.temporaryAssignmentTo
            )FROM Employee e
            JOIN Site s ON e.siteId = s.id
            JOIN Site st ON e.temporaryAssignmentSiteId = st.id
            JOIN Shift sh ON e.shiftId = sh.id
            JOIN Department d ON e.departmentId = d.id
            JOIN Team t ON e.teamId = t.id
            JOIN Position p ON e.positionId = p.id
            JOIN Agency a ON e.agencyId = a.id
            JOIN Country c ON e.countryId = c.id
            JOIN EmployeeDetails ai ON e.id = ai.employee.id
            LEFT JOIN SupervisorAssignment es ON es.employeeId = e.id
            LEFT JOIN Employee supervisor ON es.supervisorId = supervisor.id
            WHERE e.expertis LIKE CONCAT(:query, '%')
               OR LOWER(e.firstName) LIKE LOWER(CONCAT(:query, '%'))
               OR LOWER(e.lastName) LIKE LOWER(CONCAT(:query, '%'))
    """)
    List<EmployeeFullInformationDTO> searchByAnyField(@Param("query") String query);

    @Query("""
    SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO(
            e.id, e.expertis, e.brCode, e.firstName, e.lastName, e.sex,
            t.name, s.name, sh.name, c.name, d.name, p.name, e.isSupervisor, e.isCanHasAccount, e.validToAccount, e.validFromAccount, a.name, ai.note, ai.dateStartContract, ai.dateFinishContract,
            ai.dateBhpNow, ai.dateBhpFuture, ai.dateAdrNow, ai.dateAdrFuture, ai.fte, COALESCE(CONCAT(supervisor.firstName, ' ', supervisor.lastName), 'No Supervisor'), COALESCE(supervisor.expertis, 'No Supervisor'), st.name, e.temporaryAssignmentFrom, e.temporaryAssignmentTo
            )FROM Employee e
            JOIN Site s ON e.siteId = s.id
            JOIN Site st ON e.temporaryAssignmentSiteId = st.id
            JOIN Shift sh ON e.shiftId = sh.id
            JOIN Department d ON e.departmentId = d.id
            JOIN Team t ON e.teamId = t.id
            JOIN Position p ON e.positionId = p.id
            JOIN Agency a ON e.agencyId = a.id
            JOIN Country c ON e.countryId = c.id
            JOIN EmployeeDetails ai ON e.id = ai.employee.id
            LEFT JOIN SupervisorAssignment es ON es.employeeId = e.id
            LEFT JOIN Employee supervisor ON es.supervisorId = supervisor.id
            WHERE LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT(:first, ' ', :second, '%'))
                OR LOWER(CONCAT(e.lastName, ' ', e.firstName)) LIKE LOWER(CONCAT(:first, ' ', :second, '%'))
    """)
    List<EmployeeFullInformationDTO> searchByFullNameVariants(@Param("first") String first, @Param("second") String second);


    @Query(value = """
                   SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO(
                   e.id, e.expertis, e.firstName, e.lastName, d.name, t.name, p.name, s.name
                   ) FROM Employee e
                   JOIN Department d ON e.departmentId = d.id
                   JOIN Team t ON e.teamId = t.id
                   JOIN Position p ON e.positionId = p.id
                   JOIN Attendance at ON at.employee.id = e.id
                   JOIN ShiftTimeWork stw ON stw.id = at.shift.id
                   JOIN Site s ON s.id = e.siteId
                   WHERE at.date = :date
                   AND at.status.statusName = 'Planed Work Day'
                   AND stw.startTime < :start
                   AND stw.endTime >= :end
                   AND p.id IN :positions
                   AND NOT EXISTS (
                                 SELECT td.id
                                 FROM TrainingDocument td
                                 JOIN td.document doc
                                 WHERE td.employee.id = e.id
                                   AND doc.name = :nameDoc
                                  )
                """)
    List<PreviewEmployeeDTO> findAllEmployeesWithPlannedShiftMatching(@Param("date") LocalDate date, @Param("start") LocalTime start, @Param("end") LocalTime end, @Param("positions") List<Integer> positions, @Param("nameDoc") String nameDoc);
}
