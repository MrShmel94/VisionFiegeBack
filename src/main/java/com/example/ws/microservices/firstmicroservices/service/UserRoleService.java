package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.request.AssignRoleUserRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRoleService {

    void assignRoleToUser(AssignRoleUserRequest requestModel);
    void removeRoleFromUser(String userId, String roleName);
    void cleanupExpiredRoles();
    List<UserRoleDTO> getAllRolePerUserId(Long userId);
    List<UserRoleDTO> getAllRolesByUserIds(List<Long> userIds);
}
