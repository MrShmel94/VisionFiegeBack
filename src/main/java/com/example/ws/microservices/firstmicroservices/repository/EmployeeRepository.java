package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.InformationContractDTO;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT new com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO("
            + "e.id, e.expertis, e.zalosId, e.brCode, "
            + "e.firstName, e.lastName, e.isWork, e.sex, "
            + "s.name, sh.name, d.name, t.name, p.name, a.name, e.isCanHasAccount, e.isSupervisor, e.validToAccount, e.validFromAccount) "
            + "FROM Employee e "
            + "JOIN Site s ON e.siteId = s.id "
            + "JOIN Shift sh ON e.shiftId = sh.id "
            + "JOIN Department d ON e.departmentId = d.id "
            + "JOIN Team t ON e.teamId = t.id "
            + "JOIN Position p ON e.positionId = p.id "
            + "JOIN Agency a ON e.agencyId = a.id "
            + "WHERE e.expertis = :expertis")
    Optional<EmployeeDTO> findEmployeeByExpertis(@Param("expertis") String expertis);

    @Query("SELECT new com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO("
            + "e.id, e.expertis, e.zalosId, e.brCode, "
            + "e.firstName, e.lastName, e.isWork, e.sex, "
            + "s.name, sh.name, d.name, t.name, p.name, a.name, e.isCanHasAccount, e.isSupervisor, e.validToAccount, e.validFromAccount) "
            + "FROM Employee e "
            + "JOIN Site s ON e.siteId = s.id "
            + "JOIN Shift sh ON e.shiftId = sh.id "
            + "JOIN Department d ON e.departmentId = d.id "
            + "JOIN Team t ON e.teamId = t.id "
            + "JOIN Position p ON e.positionId = p.id "
            + "JOIN Agency a ON e.agencyId = a.id "
            + "WHERE e.expertis IN :expertisList")
    List<EmployeeDTO> findEmployeesByExpertisList(@Param("expertisList") List<String> expertisList);

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
            WHERE e.expertis IN :expertisList
            """)
    Page<EmployeeFullInformationDTO> findEmployeeFullInformationByExpertisListPageable(@Param("expertisList") List<String> expertisList, Pageable pageable);

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
            WHERE e.expertis IN :expertisList
            """)
    List<EmployeeFullInformationDTO> findEmployeeFullInformationByExpertisList(@Param("expertisList") List<String> expertisList);

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
            WHERE e.expertis = :expertis
            """)
    Optional<EmployeeFullInformationDTO> findEmployeeFullInformationByExpertis(@Param("expertis") String expertis);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.InformationContractDTO(
           e.expertis, ae.dateStartContract, ae.dateFinishContract
           ) FROM Employee e
           LEFT JOIN AiEmployee ae ON ae.id = e.id
           WHERE e.expertis IN :expertisList
           """)
    List<InformationContractDTO> getAllInformationContractsByListExertis(@Param("expertisList") List<String> listExertis);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO(
           e.expertis, e.firstName, e.lastName, d.name, t.name
           ) FROM Employee e
           JOIN Department d ON e.departmentId = d.id
           JOIN Team t ON e.teamId = t.id
           JOIN Site s ON e.siteId = s.id
           WHERE e.isWork = true
           AND s.name = :siteName
           AND e.id NOT IN (SELECT es.employeeId FROM EmployeeSupervisor es)
           """)
    List<PreviewEmployeeDTO> getEmployeeWithoutSupervisor(@Param("siteName") String siteName);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO(
           e.expertis, e.firstName, e.lastName, d.name, t.name
           ) FROM Employee e
           JOIN Department d ON e.departmentId = d.id
           JOIN Team t ON e.teamId = t.id
           JOIN Site s ON e.siteId = s.id
           WHERE e.isWork = true
           AND s.name = :siteName
           AND e.isSupervisor = true
           """)
    List<PreviewEmployeeDTO> getSupervisors(@Param("siteName") String siteName);
}
