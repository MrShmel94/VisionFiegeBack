package com.example.ws.microservices.firstmicroservices.serviceImpl.config;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.*;
import com.example.ws.microservices.firstmicroservices.response.ConfigurationRegistrationDTO;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ConfigurationService {

    private final RedisCacheService redisCacheService;

    public ConfigurationRegistrationDTO getConfigurationForSite(String siteName) {
        return ConfigurationRegistrationDTO.builder()
                .agencies(filterBySite("agency:*", AgencyDTO.class, siteName))
                .countries(filterBySite("country:*", CountryDTO.class, siteName))
                .departments(filterBySite("department:*", DepartmentDTO.class, siteName))
                .positions(filterBySite("position:*", PositionDTO.class, siteName))
                .shifts(filterBySite("shift:*", ShiftDTO.class, siteName))
                .teams(filterBySite("team:*", TeamDTO.class, siteName))
                .build();
    }

    private <T extends SiteAware> List<T> filterBySite(String keyPattern, Class<T> type, String siteName) {
        return redisCacheService.getAllFromCache(keyPattern, type).stream()
                .filter(obj -> obj.getSiteName().equals(siteName))
                .toList();
    }
}
