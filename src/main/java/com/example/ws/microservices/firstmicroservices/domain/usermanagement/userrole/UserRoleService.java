package com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.request.AssignRoleUserRequest;

import java.util.List;

public interface UserRoleService {

    void assignRoleToUser(AssignRoleUserRequest requestModel);
    void removeRoleFromUser(String userId, String roleName);
    void cleanupExpiredRoles();
    List<UserRoleDTO> getAllRolePerUserId(Long userId);
    List<UserRoleDTO> getAllRolesByUserIds(List<Long> userIds);
}
