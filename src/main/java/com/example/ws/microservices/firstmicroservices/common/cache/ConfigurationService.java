package com.example.ws.microservices.firstmicroservices.common.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeesupervisor.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.agency.AgencyDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country.CountryDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position.PositionDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift.ShiftDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team.TeamDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.dto.SiteAware;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.ConfigurationRegistrationDTO;
import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ConfigurationService {

    private final RedisCacheService redisCacheService;
    private final UserService userService;

//    public ConfigurationRegistrationDTO getConfigurationForSite() {
//
//        log.info("Fetching supervisors for the current user.");
//        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
//        log.debug("Current user ID: {}", currentUser.getUserId());
//
//        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(null, currentUser.getUserId());
//        log.debug("Retrieved supervisor information for user: {} with siteName: {}", currentUser.getUserId(), allInformation.getSiteName());
//
//        return ConfigurationRegistrationDTO.builder()
//                .siteName(allInformation.getSiteName())
//                .agencies(filterBySite("agency:*", AgencyDTO.class, allInformation.getSiteName()))
//                .countries(filterBySite("country:*", CountryDTO.class, allInformation.getSiteName()))
//                .departments(filterBySite("department:*", DepartmentDTO.class, allInformation.getSiteName()))
//                .positions(filterBySite("position:*", PositionDTO.class, allInformation.getSiteName()))
//                .shifts(filterBySite("shift:*", ShiftDTO.class, allInformation.getSiteName()))
//                .teams(filterBySite("team:*", TeamDTO.class, allInformation.getSiteName()))
//                .build();
//    }

    public ConfigurationRegistrationDTO getConfigurationForSite() {

        log.info("Fetching supervisors for the current user.");
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        log.debug("Current user ID: {}", currentUser.getUserId());

        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(null, currentUser.getUserId());
        log.debug("Retrieved supervisor information for user: {} with siteName: {}", currentUser.getUserId(), allInformation.getSiteName());

        return ConfigurationRegistrationDTO.builder()
                .siteName(allInformation.getSiteName())
                .agencies(filterBySite("agencies", new TypeReference<List<AgencyDTO>>() {
                }, allInformation.getSiteName()))
                .countries(filterBySite("countries", new TypeReference<List<CountryDTO>>() {
                }, allInformation.getSiteName()))
                .departments(filterBySite("departments", new TypeReference<List<DepartmentDTO>>() {
                }, allInformation.getSiteName()))
                .positions(filterBySite("positions", new TypeReference<List<PositionDTO>>() {
                }, allInformation.getSiteName()))
                .shifts(filterBySite("shifts", new TypeReference<List<ShiftDTO>>() {
                }, allInformation.getSiteName()))
                .teams(filterBySite("teams", new TypeReference<List<TeamDTO>>() {
                }, allInformation.getSiteName()))
                .build();
    }

    public ConfigurationRegistrationDTO getConfigurationForSite(String siteName) {

        return ConfigurationRegistrationDTO.builder()
                .siteName(siteName)
                .agencies(filterBySite("agencies", new TypeReference<List<AgencyDTO>>() {
                }, siteName))
                .countries(filterBySite("countries", new TypeReference<List<CountryDTO>>() {
                }, siteName))
                .departments(filterBySite("departments", new TypeReference<List<DepartmentDTO>>() {
                }, siteName))
                .positions(filterBySite("positions", new TypeReference<List<PositionDTO>>() {
                }, siteName))
                .shifts(filterBySite("shifts", new TypeReference<List<ShiftDTO>>() {
                }, siteName))
                .teams(filterBySite("teams", new TypeReference<List<TeamDTO>>() {
                }, siteName))
                .build();
    }

    private <T extends SiteAware> List<T> filterBySite(String keyPattern, Class<T> type, String siteName) {
        return redisCacheService.getAllFromCache(keyPattern, type).stream()
                .filter(obj -> obj.getSiteName().equals(siteName))
                .toList();
    }

    private <T extends SiteAware> List<T> filterBySite(String keyPattern, TypeReference<List<T>> typeReference, String siteName) {
        List<T> allObject = redisCacheService.getFromCache(keyPattern, typeReference).orElseGet(Collections::emptyList);
        return allObject.stream()
                .filter(obj -> obj.getSiteName().equals(siteName))
                .toList();
    }
}
