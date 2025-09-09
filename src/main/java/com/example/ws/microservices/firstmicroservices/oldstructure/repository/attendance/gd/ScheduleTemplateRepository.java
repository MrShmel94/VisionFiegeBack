package com.example.ws.microservices.firstmicroservices.oldstructure.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.attendance.gd.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    Optional<ScheduleTemplate> findByNameScheduleTemplate(String templateName);
    List<ScheduleTemplate> findAllByUserId(String userId);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO(
           e.id, e.expertis, e.firstName, e.lastName, d.name, t.name, p.name, s.name
           ) FROM Employee e
           JOIN Department d ON e.departmentId = d.id
           JOIN Team t ON e.teamId = t.id
           JOIN Position p ON e.positionId = p.id
           JOIN Site s ON s.id = e.siteId
           LEFT JOIN Attendance att ON e.id = att.employee.id AND att.date BETWEEN :startDate AND :endDate
               WHERE e.isWork = true
               AND att.id IS NULL
           """)
    List<PreviewEmployeeDTO> getAllEmployeeWithoutScheduleTemplateBeetwenDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<ScheduleTemplate> findAllByDate(LocalDate date);
}
