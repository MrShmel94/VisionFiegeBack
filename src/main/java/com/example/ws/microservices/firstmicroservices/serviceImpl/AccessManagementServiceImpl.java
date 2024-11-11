package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.service.AccessManagementService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeSupervisorService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessManagementServiceImpl implements AccessManagementService {

    private final EmployeeSupervisorService supervisorService;
    private final RedisCacheService redisCacheService;

    @Override
    public void grantTemporaryAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo) {
        supervisorService.addAccess(employeeId, supervisorExpertis, validTo);
        redisCacheService.updateSupervisorAccess(supervisorExpertis, List.of(employeeId.toString()));
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Override
    public void cleanupExpiredAccess() {
        List<EmployeeSupervisor> expiredAccesses = supervisorService.getExpiredAccesses();

        for (EmployeeSupervisor access : expiredAccesses) {
            supervisorService.revokeAccess(access.getEmployeeId(), access.getSupervisorExpertis());
            redisCacheService.removeExpiredAccess(access.getSupervisorExpertis(), access.getEmployeeId().toString());
        }
    }
}
