package com.example.ws.microservices.firstmicroservices.service;

import java.time.LocalDateTime;

public interface AccessManagementService {

    void grantTemporaryAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo);
    void cleanupExpiredAccess();
}
