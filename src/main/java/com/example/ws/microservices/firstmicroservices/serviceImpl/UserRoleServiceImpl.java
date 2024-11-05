package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.entity.role.Role;
import com.example.ws.microservices.firstmicroservices.repository.UserRoleRepository;
import com.example.ws.microservices.firstmicroservices.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        return userRoleRepository.findRolesByUserId(userId);
    }
}
