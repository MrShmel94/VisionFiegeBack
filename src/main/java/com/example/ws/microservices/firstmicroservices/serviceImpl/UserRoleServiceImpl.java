package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.entity.Employee;
import com.example.ws.microservices.firstmicroservices.entity.role.Role;
import com.example.ws.microservices.firstmicroservices.entity.role.UserRole;
import com.example.ws.microservices.firstmicroservices.entity.role.UserRoleId;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeRepository;
import com.example.ws.microservices.firstmicroservices.repository.RoleRepository;
import com.example.ws.microservices.firstmicroservices.repository.UserRoleRepository;
import com.example.ws.microservices.firstmicroservices.service.UserRoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<UserRole> getAllRoles() {
        return userRoleRepository.findAll();
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, String roleName, LocalDateTime validFrom, LocalDateTime validTo) {
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));

        UserRole userRole = new UserRole();
        //userRole.setId(new UserRoleId(userId, role.getId()));
        userRole.setUser(employee);
        userRole.setRole(role);
        userRole.setValidFrom(validFrom != null ? validFrom : LocalDateTime.now());
        userRole.setValidTo(validTo != null ? validTo : LocalDateTime.of(9999, 12, 31, 23, 59, 59));

        userRoleRepository.save(userRole);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));

        userRoleRepository.deleteByUserIdAndRoleId(userId, role.getId());
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void cleanupExpiredRoles() {
        List<UserRole> expiredRoles = userRoleRepository.findExpiredRoles(LocalDateTime.now());
        userRoleRepository.deleteAll(expiredRoles);
    }

    @Override
    public List<RoleDTO> getAllRolePerUserId(Long userId) {
        List<RoleDTO> roles = roleRepository.findAllRolesByUserId(userId);
        if (roles.isEmpty()) {
            throw new EntityNotFoundException("No roles found for user ID: " + userId);
        }
        return roles;
    }
}
