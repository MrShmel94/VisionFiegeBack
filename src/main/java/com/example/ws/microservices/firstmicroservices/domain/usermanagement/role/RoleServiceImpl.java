package com.example.ws.microservices.firstmicroservices.domain.usermanagement.role;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RedisService redisService;

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    @Override
    public UserRoleDTO getRoleDTOByName(String name) {
        Optional<UserRoleDTO> roleDTO = redisService.getFromCache("role:" + name, UserRoleDTO.class);
        if(roleDTO.isEmpty()){
            Role role = findRoleByName(name);
            UserRoleDTO newDto = UserRoleDTO.builder()
                    .roleId(role.getId())
                    .name(role.getName())
                    .weight(role.getWeight())
                    .build();
            redisService.saveToCache("role:" + name, newDto);
            return newDto;
        }
        return roleDTO.get();
    }

    @Override
    public List<UserRoleDTO> getAllRoleByUserId(Long userId) {
        return roleRepository.findAllRolesByUserId(userId);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        List<RoleDTO> userRoleDTO = redisService.getFromCache("roles", new TypeReference<List<RoleDTO>>() {})
                .orElseGet(() -> {
                    List<RoleDTO> userRoles = roleRepository.findAll()
                            .stream().map(role -> RoleDTO.builder()
                                    .id(role.getId())
                                    .description(role.getDescription())
                                    .weight(role.getWeight())
                                    .name(role.getName())
                                    .build()).toList();

                    redisService.saveToCache("roles", userRoles);

                    return userRoles;
                });
        log.info("Found {} roles", userRoleDTO.size());
        return userRoleDTO;
    }
}
