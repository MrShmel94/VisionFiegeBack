package com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.customError.AuthenticationFailedException;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleId;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.role.Role;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.role.RoleRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.AssignRoleUserRequest;
import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.role.RoleService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final EmployeeService employeeService;
    private final RedisService redisService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UserService userService;

    @Override
    @Transactional
    public void assignRoleToUser(AssignRoleUserRequest requestModel) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails currentUser)) {
            throw new AccessDeniedException("Invalid authentication");
        }

        if(currentUser.getExpertis().equals(requestModel.expertis())){
            throw new AccessDeniedException("You can't prescribe roles for yourself.");
        }

        EmployeeFullInformationDTO employee = employeeService.getEmployeeFullInformation(requestModel.expertis());

        if (!employee.getSiteName().equalsIgnoreCase(currentUser.getSiteName())){
            throw new AuthenticationFailedException("Access denied: your site must match the employee’s site.");
        }

        int maxCurrentWeight = currentUser.getRoles().stream()
                .mapToInt(UserRoleDTO::getWeight)
                .max()
                .orElseThrow(() -> new AccessDeniedException("User does not have any roles assigned"));

        UserRoleDTO userRoleDto = roleService.getRoleDTOByName(requestModel.roleName());

        if (userRoleDto.getWeight() > maxCurrentWeight) {
            throw new AccessDeniedException(String.format(
                    "Access denied: You can only assign roles with weight less or equals than your maximum role weight (%d)",
                    maxCurrentWeight
            ));
        }

        SupervisorAllInformationDTO employeeDto = userService.getSupervisorAllInformation(requestModel.expertis(), null);

        UserRole userRole = new UserRole();
        userRole.setId(new UserRoleId(employeeDto.getId(), userRoleDto.getRoleId()));
        userRole.setValidFrom(requestModel.validFrom() != null ? requestModel.validFrom() : LocalDate.now());
        userRole.setValidTo(requestModel.validTo() != null ? requestModel.validTo() : LocalDate.of(9999, 12, 31));

        userRoleDto.setValidTo(userRole.getValidTo());
        userRoleDto.setValidFrom(userRole.getValidFrom());
        userRoleDto.setUserId(userRole.getId().getUserId());

        userRoleRepository.save(userRole);

        employeeDto.getRoles().add(userRoleDto);
        updateCacheForUserDetailsAndAccounts(employeeDto);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(String expertis, String roleName) {
        SupervisorAllInformationDTO employeeDto = userService.getSupervisorAllInformation(expertis, null);

        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();

        if (!employeeDto.getSiteName().equalsIgnoreCase(currentUser.getSiteName())){
            throw new AuthenticationFailedException("Access denied: your site must match the employee’s site.");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));

        userRoleRepository.deleteByUserIdAndRoleId(employeeDto.getId(), role.getId());

        employeeDto.getRoles().removeIf(roleDTO -> roleDTO.getName().equals(roleName));
        updateCacheForUserDetailsAndAccounts(employeeDto);
    }

    private void updateCacheForUserDetailsAndAccounts(SupervisorAllInformationDTO employeeDto) {
        redisService.saveToHash("userDetails:hash", employeeDto.getExpertis(), employeeDto);

        PreviewEmployeeDTO dto = redisService.getFromHash("usersAccount:hash", employeeDto.getExpertis(), PreviewEmployeeDTO.class)
                .orElse(PreviewEmployeeDTO.builder()
                        .id(employeeDto.getId())
                        .expertis(employeeDto.getExpertis())
                        .firstName(employeeDto.getFirstName())
                        .lastName(employeeDto.getLastName())
                        .department(employeeDto.getDepartmentName())
                        .team(employeeDto.getTeamName())
                        .build());

        dto.setRoles(employeeDto.getRoles());
        redisService.saveToHash("usersAccount:hash", dto.getExpertis(), dto);
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void cleanupExpiredRoles() {
        List<UserRole> expiredRoles = userRoleRepository.findExpiredRoles(LocalDateTime.now());
        userRoleRepository.deleteAll(expiredRoles);
    }

    @Override
    public List<UserRoleDTO> getAllRolePerUserId(Long userId) {
        List<UserRoleDTO> roles = roleRepository.findAllRolesByUserId(userId);
        if (roles.isEmpty()) {
            throw new EntityNotFoundException("No roles found for user ID: " + userId);
        }
        return roles;
    }

    @Override
    public List<UserRoleDTO> getAllRolesByUserIds(List<Long> userIds) {
        List<UserRoleDTO> roles = roleRepository.findUserRoleDtosByUserIds(userIds);
        if (roles.isEmpty()) {
            throw new EntityNotFoundException("No roles found for user IDs: " + userIds);
        }
        return roles;
    }
}
