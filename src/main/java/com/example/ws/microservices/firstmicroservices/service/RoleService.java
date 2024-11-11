package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.entity.role.Role;

public interface RoleService {
    Role findRoleByName(String name);
}
