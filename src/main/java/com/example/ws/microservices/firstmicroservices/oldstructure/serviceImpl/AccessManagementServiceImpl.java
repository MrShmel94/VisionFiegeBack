package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.customError.AuthenticationFailedException;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeesupervisor.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.role.RoleService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.UserRoleService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.AssignRoleUserRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.ResponseUsersNotVerification;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.AccessManagementService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.EmployeeSupervisorService;
import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.common.security.aspects.AccessControl;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessManagementServiceImpl implements AccessManagementService {

    private final EmployeeSupervisorService supervisorService;
    private final EmployeeService employeeService;
    private final RedisCacheService redisCacheService;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final RoleService roleService;


    @Scheduled(cron = "0 0 * * * ?")
    @Override
    public void cleanupExpiredAccess() {
        //List<EmployeeSupervisor> expiredAccesses = supervisorService.getExpiredAccesses();

//        for (EmployeeSupervisor access : expiredAccesses) {
//            supervisorService.revokeAccess(access.getEmployeeId(), access.getSupervisorExpertis());
//            redisCacheService.removeExpiredAccess(access.getSupervisorExpertis(), access.getEmployeeId().toString());
//        }
    }

    @Override
    @Transactional
    @AccessControl(
            minWeight = 35
    )
    public void verifyAccountAndAssignRole(AssignRoleUserRequest requestModel) {
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        EmployeeFullInformationDTO employee = employeeService.getEmployeeFullInformation(requestModel.expertis());

        if (!employee.getSiteName().equalsIgnoreCase(currentUser.getSiteName())){
            throw new AuthenticationFailedException("Access denied: your site must match the employee’s site.");
        }

        userService.verifyUserAccount(requestModel.expertis());
        userRoleService.assignRoleToUser(requestModel);

        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(requestModel.expertis(), null);
        allInformation.setIsVerified(true);

        redisCacheService.saveToHash("userDetails:hash", allInformation.getExpertis(), allInformation);
        redisCacheService.deleteFromHash("usersNotVerification:hash", allInformation.getExpertis());

        log.info("Account verified and role assigned for user: {}", requestModel.expertis());
    }

    @Override
    public ResponseUsersNotVerification getAllUsersNotVerified() {
        Map<String, PreviewEmployeeDTO> allDto = redisCacheService.getAllFromHash("usersNotVerification:hash", new TypeReference<PreviewEmployeeDTO>() {});
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();

        if(allDto.isEmpty()){
            List<PreviewEmployeeDTO> list = userService.getAllUsersWithoutVerification();

            Map<String, PreviewEmployeeDTO> mapPreview = list.stream()
                    .collect(Collectors.toMap(PreviewEmployeeDTO::getExpertis, Function.identity(), (a, b) -> a));

            redisCacheService.saveAllToHash("usersNotVerification:hash", mapPreview);
        }


        log.info("Employee without verification: {}", allDto.size());
        return ResponseUsersNotVerification.builder()
                .users(allDto.values().stream().filter(dto -> dto.getSiteName().equalsIgnoreCase(currentUser.getSiteName())).toList())
                .roles(roleService.getAllRoles())
                .build();
    }

    @Override
    public ResponseUsersNotVerification getAllUsersAccount() {
        Map<String, PreviewEmployeeDTO> allDto = redisCacheService.getAllFromHash("usersAccount:hash", new TypeReference<PreviewEmployeeDTO>() {});
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();

        if(allDto.isEmpty()){
            List<PreviewEmployeeDTO> list = userService.getAllUsersVerification();
            List<UserRoleDTO> rolesDto = userRoleService.getAllRolesByUserIds(list.stream().map(PreviewEmployeeDTO::getId).toList());
            Map<Long, List<UserRoleDTO>> mapRoleDto = rolesDto.stream().collect(Collectors.groupingBy(UserRoleDTO::getUserId));
            list.forEach(dto -> dto.setRoles(mapRoleDto.getOrDefault(dto.getId(), List.of())));

            Map<String, PreviewEmployeeDTO> mapPreview = list.stream()
                    .collect(Collectors.toMap(PreviewEmployeeDTO::getExpertis, Function.identity(), (a, b) -> a));

            redisCacheService.saveAllToHash("usersAccount:hash", mapPreview);
        }

        log.info("Employee account verification: {}", allDto.size());
        return ResponseUsersNotVerification.builder()
                .users(allDto.values().stream().filter(dto -> dto.getSiteName().equalsIgnoreCase(currentUser.getSiteName())).toList())
                .roles(roleService.getAllRoles())
                .build();
    }
}
