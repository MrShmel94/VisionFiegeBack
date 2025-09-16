package com.example.ws.microservices.firstmicroservices.oldstructure.repository.etc.planed;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.planed.PlanedEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.planed.PlanedEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanedEmployeeRepository extends JpaRepository<PlanedEmployee, Long> {

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.planed.PlanedEmployeeDTO(
           pe.id, pe.planedTraining.id, CONCAT(emp.firstName, ' ' , emp.lastName), emp.expertis, emp.id, emp.sex, dep.name, team.name, shift.name,
           COALESCE(CONCAT(supervisor.firstName, ' ', supervisor.lastName), 'No Supervisor'), COALESCE(supervisor.expertis, 'No Supervisor'), pe.isPresent, pe.date, CONCAT(fnUser.firstName, ' ' , fnUser.lastName)
           ) FROM PlanedEmployee pe
           JOIN Employee emp ON pe.employee.id = emp.id
           JOIN Department dep ON emp.departmentId = dep.id
           JOIN Team team ON emp.teamId = team.id
           JOIN Shift shift ON emp.shiftId = shift.id
           LEFT JOIN SupervisorAssignment es ON es.employeeId = pe.employee.id
           JOIN Employee supervisor ON supervisor.id = es.supervisorId
           JOIN UserEntity ui ON ui.userId = pe.userId
           JOIN Employee fnUser ON fnUser.expertis = ui.expertis
           WHERE pe.planedTraining.id IN :idS
           """)
    List<PlanedEmployeeDTO> getAllEmployeeDTOByIdTraining(@Param("idS") List<Long> ids);
}
