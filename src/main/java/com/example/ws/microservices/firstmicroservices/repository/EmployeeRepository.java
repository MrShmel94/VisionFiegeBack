package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT new com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO("
            + "e.id, e.expertis, e.zalosId, e.brCode, "
            + "e.firstName, e.lastName, e.isWork, e.sex, "
            + "s.name, sh.name, d.name, t.name, p.name, a.name) "
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
            + "s.name, sh.name, d.name, t.name, p.name, a.name) "
            + "FROM Employee e "
            + "JOIN Site s ON e.siteId = s.id "
            + "JOIN Shift sh ON e.shiftId = sh.id "
            + "JOIN Department d ON e.departmentId = d.id "
            + "JOIN Team t ON e.teamId = t.id "
            + "JOIN Position p ON e.positionId = p.id "
            + "JOIN Agency a ON e.agencyId = a.id "
            + "WHERE e.expertis IN :expertisList")
    List<EmployeeDTO> findEmployeesByExpertisList(@Param("expertisList") List<String> expertisList);
}
