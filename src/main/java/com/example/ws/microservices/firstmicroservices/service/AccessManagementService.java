package com.example.ws.microservices.firstmicroservices.service;

import java.time.LocalDateTime;
import java.util.List;

public interface AccessManagementService {

    void grantTemporaryAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo);
    void grantAccountCreatingAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo);
    void cleanupExpiredAccess();
    void verifyAccountAndAssignRole(String userId);
    List<?> getAllUsersNotVerified();
}
