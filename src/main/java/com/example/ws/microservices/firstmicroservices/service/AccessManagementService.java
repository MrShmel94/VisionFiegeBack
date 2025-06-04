package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.AssignRoleUserRequest;
import com.example.ws.microservices.firstmicroservices.response.ResponseUsersNotVerification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AccessManagementService {

    void cleanupExpiredAccess();
    void verifyAccountAndAssignRole(AssignRoleUserRequest requestModel);
    ResponseUsersNotVerification getAllUsersNotVerified();
    ResponseUsersNotVerification getAllUsersAccount();
}
