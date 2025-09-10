package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeesupervisor.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final UserService userService;
    private final DepartmentServiceCache departmentServiceCache;
    private final RedisCacheService redisCacheService;

    @Override
    public List<DepartmentDTO> getDepartments() {
        return redisCacheService.getFromCache("departments", new TypeReference<List<DepartmentDTO>>() {}).orElseGet(() -> {
            List<DepartmentDTO> allDto = departmentServiceCache.getAllFromDB();
            redisCacheService.saveToCache("departments", allDto);

            return allDto;
        });
    }

    @Override
    public List<DepartmentDTO> getDepartmentsBySupervisorSite() {

        CustomUserDetails users = new SecurityUtils().getCurrentUser();
        SupervisorAllInformationDTO usersDTO = userService.getSupervisorAllInformation(null, users.getUserId());

        return getDepartments().stream().filter(dep -> dep.getSiteName().equalsIgnoreCase(usersDTO.getSiteName())).toList();
    }

    @Override
    public List<DepartmentDTO> getDepartmentsBySupervisorSite(String userId) {
        SupervisorAllInformationDTO usersDTO = userService.getSupervisorAllInformation(null, userId);

        return getDepartments().stream().filter(dep -> dep.getSiteName().equalsIgnoreCase(usersDTO.getSiteName())).toList();
    }
}
