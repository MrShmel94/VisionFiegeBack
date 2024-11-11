package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisorId;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeSupervisorRepository;
import com.example.ws.microservices.firstmicroservices.service.EmployeeSupervisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeSupervisorServiceImpl implements EmployeeSupervisorService {

    private final EmployeeSupervisorRepository repository;

    public void addAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo) {
        EmployeeSupervisor access = new EmployeeSupervisor();
        access.setEmployeeId(employeeId);
        access.setSupervisorExpertis(supervisorExpertis);
        access.setValidFrom(LocalDateTime.now());
        access.setValidTo(validTo);
        repository.save(access);
    }

    public void revokeAccess(Long employeeId, String supervisorExpertis) {
        repository.deleteById(new EmployeeSupervisorId(employeeId, supervisorExpertis));
    }

    public List<EmployeeSupervisor> getActiveAccesses() {
        return repository.findActiveAccesses(LocalDateTime.now());
    }

    public List<EmployeeSupervisor> getExpiredAccesses() {
        return repository.findExpiredAccesses(LocalDateTime.now());
    }

    public List<EmployeeSupervisor> findBySupervisorExpertis(String supervisorExpertis) {
        return repository.findBySupervisorExpertis(supervisorExpertis);
    }

    public List<EmployeeSupervisor> findByEmployeeId(Long employeeId) {
        return repository.findByEmployeeId(employeeId);
    }
}
