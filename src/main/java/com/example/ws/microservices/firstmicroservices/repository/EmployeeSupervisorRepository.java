package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeSupervisorRepository extends JpaRepository<EmployeeSupervisor, EmployeeSupervisorId> {

    @Query("SELECT es FROM EmployeeSupervisor es WHERE es.validTo > :currentDate")
    List<EmployeeSupervisor> findActiveAccesses(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT es FROM EmployeeSupervisor es WHERE es.validTo < :currentDate")
    List<EmployeeSupervisor> findExpiredAccesses(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT es FROM EmployeeSupervisor es WHERE es.supervisorExpertis = :expertis")
    List<EmployeeSupervisor> findBySupervisorExpertis(@Param("expertis") String supervisorExpertis);

    @Query("SELECT es FROM EmployeeSupervisor es WHERE es.employeeId = :employeeId")
    List<EmployeeSupervisor> findByEmployeeId(@Param("employeeId") Long employeeId);
}
