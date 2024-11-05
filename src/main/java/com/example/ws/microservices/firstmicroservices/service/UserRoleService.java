package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.entity.role.Role;

import java.util.List;

public interface UserRoleService {

    List<Role> findRolesByUserId(Long userId);
}
