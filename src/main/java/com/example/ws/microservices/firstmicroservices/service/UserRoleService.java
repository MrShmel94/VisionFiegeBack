package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.entity.role.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRoleService {

    List<UserRole> getAllRoles();
    void assignRoleToUser(Long userId, String roleName, LocalDateTime validFrom, LocalDateTime validTo);
    void removeRoleFromUser(Long userId, String roleName);
    void cleanupExpiredRoles();
    List<RoleDTO> getAllRolePerUserId(Long userId);
}
