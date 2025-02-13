package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.entity.role.Role;
import com.example.ws.microservices.firstmicroservices.entity.role.UserRole;
import com.example.ws.microservices.firstmicroservices.entity.role.UserRoleId;
import com.example.ws.microservices.firstmicroservices.repository.RoleRepository;
import com.example.ws.microservices.firstmicroservices.repository.UserRoleRepository;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.service.RoleService;
import com.example.ws.microservices.firstmicroservices.service.UserRoleService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RedisCacheService redisCacheService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UserService userService;

    @Override
    @Transactional
    public void assignRoleToUser(String userId, String roleName, LocalDateTime validFrom, LocalDateTime validTo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails currentUser)) {
            throw new AccessDeniedException("Invalid authentication");
        }

        if(currentUser.getUserId().equals(userId)){
            throw new AccessDeniedException("You can't prescribe roles for yourself.");
        }

        int maxCurrentWeight = currentUser.getRoles().stream()
                .mapToInt(RoleDTO::getWeight)
                .max()
                .orElseThrow(() -> new AccessDeniedException("User does not have any roles assigned"));

        RoleDTO roleDto = roleService.getRoleDTOByName(roleName);

        if (roleDto.getWeight() >= maxCurrentWeight) {
            throw new AccessDeniedException(String.format(
                    "Access denied: You can only assign roles with weight less than your maximum role weight (%d)",
                    maxCurrentWeight
            ));
        }

        SupervisorAllInformationDTO employeeDto = userService.getSupervisorAllInformation(null, userId);


        UserRole userRole = new UserRole();
        userRole.setId(new UserRoleId(employeeDto.getId(), roleDto.getRoleId()));
        userRole.setValidFrom(validFrom != null ? validFrom : LocalDateTime.now());
        userRole.setValidTo(validTo != null ? validTo : LocalDateTime.of(9999, 12, 31, 23, 59, 59));

        userRoleRepository.save(userRole);

        employeeDto.getRoles().add(roleDto);
        redisCacheService.saveToCache("userDetails:" + employeeDto.getExpertis(), employeeDto);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(String userId, String roleName) {
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
