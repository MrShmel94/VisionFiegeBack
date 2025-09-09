package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.request.AssignRoleUserRequest;
import com.example.ws.microservices.firstmicroservices.response.ResponseUsersNotVerification;

public interface AccessManagementService {

    void cleanupExpiredAccess();
    void verifyAccountAndAssignRole(AssignRoleUserRequest requestModel);
    ResponseUsersNotVerification getAllUsersNotVerified();
    ResponseUsersNotVerification getAllUsersAccount();
}
