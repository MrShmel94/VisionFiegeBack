package com.example.ws.microservices.firstmicroservices.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AccessManagementService {

    void cleanupExpiredAccess();
    void verifyAccountAndAssignRole(String userId);
    List<?> getAllUsersNotVerified();
}
