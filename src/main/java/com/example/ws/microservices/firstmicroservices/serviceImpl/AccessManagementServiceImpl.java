package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.service.AccessManagementService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeSupervisorService;
import com.example.ws.microservices.firstmicroservices.service.UserRoleService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessManagementServiceImpl implements AccessManagementService {

    private final EmployeeSupervisorService supervisorService;
    private final RedisCacheService redisCacheService;
    private final UserService userService;
    private final UserRoleService userRoleService;

    @Override
    public void grantTemporaryAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo) {
        supervisorService.addAccess(employeeId, supervisorExpertis, validTo);
        redisCacheService.updateSupervisorAccess(supervisorExpertis, List.of(employeeId.toString()));
    }

    @Override
    public void grantAccountCreatingAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo) {

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

    @Override
    public void verifyAccountAndAssignRole(String userId) {
        userService.verifyUserAccount(userId);

        userRoleService.assignRoleToUser(userId, "User", null, null);

        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(null, userId);
        allInformation.setIsVerified(true);
        redisCacheService.saveToCache("userDetails:" + allInformation.getExpertis(), allInformation);

        log.info("Account verified and role assigned for user: {}", userId);
    }

    @Override
    public List<?> getAllUsersNotVerified() {
        return List.of();
    }
}
