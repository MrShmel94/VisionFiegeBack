package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.entity.role.Role;
import com.example.ws.microservices.firstmicroservices.repository.RoleRepository;
import com.example.ws.microservices.firstmicroservices.service.RoleService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import lombok.AllArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RedisCacheService redisCacheService;

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    @Override
    public RoleDTO getRoleDTOByName(String name) {
        Optional<RoleDTO> roleDTO = redisCacheService.getFromCache("role:" + name, RoleDTO.class);
        if(roleDTO.isEmpty()){
            Role role = findRoleByName(name);
            RoleDTO newDto = RoleDTO.builder()
                    .roleId(role.getId())
                    .name(role.getName())
                    .weight(role.getWeight())
                    .build();
            redisCacheService.saveToCache("role:" + name, newDto);
            return newDto;
        }
        throw new RuntimeException("Role not found: " + name);
    }

    @Override
    public List<RoleDTO> getAllRoleByUserId(Long userId) {
        return roleRepository.findAllRolesByUserId(userId);
    }

}
