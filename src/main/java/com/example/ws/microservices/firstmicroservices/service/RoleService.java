package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.role.Role;

import java.util.List;

public interface RoleService {
    Role findRoleByName(String name);
    UserRoleDTO getRoleDTOByName(String name);
    List<UserRoleDTO> getAllRoleByUserId(Long userId);

    List<RoleDTO> getAllRoles();
}
