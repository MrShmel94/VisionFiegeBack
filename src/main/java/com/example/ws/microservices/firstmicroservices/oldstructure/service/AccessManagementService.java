package com.example.ws.microservices.firstmicroservices.oldstructure.service;

import com.example.ws.microservices.firstmicroservices.oldstructure.request.AssignRoleUserRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.ResponseUsersNotVerification;

public interface AccessManagementService {

    void cleanupExpiredAccess();
    void verifyAccountAndAssignRole(AssignRoleUserRequest requestModel);
    ResponseUsersNotVerification getAllUsersNotVerified();
    ResponseUsersNotVerification getAllUsersAccount();
}
