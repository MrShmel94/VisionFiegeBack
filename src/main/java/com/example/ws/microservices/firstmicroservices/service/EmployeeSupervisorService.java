package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.request.RequestSetEmployeeToSupervisor;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeSupervisorService {

    void addAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo);
    List<String> addAccess(List<RequestSetEmployeeToSupervisor> employees);
    void revokeAccess(Long employeeId, String supervisorExpertis);
    List<EmployeeSupervisor> getActiveAccesses();
    List<EmployeeSupervisor> getExpiredAccesses();
    List<EmployeeSupervisor> findByEmployeeId(Long employeeId);
}
