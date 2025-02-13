package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.AgencyDTO;
import com.example.ws.microservices.firstmicroservices.entity.role.Role;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface RoleService {
    Role findRoleByName(String name);
    RoleDTO getRoleDTOByName(String name);

    List<RoleDTO> getAllRoleByUserId(Long userId);
}
