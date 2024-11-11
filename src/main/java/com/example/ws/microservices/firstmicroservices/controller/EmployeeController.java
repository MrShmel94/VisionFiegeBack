package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.*;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;
import com.example.ws.microservices.firstmicroservices.response.ConfigurationRegistrationDTO;
import com.example.ws.microservices.firstmicroservices.service.EmployeeMappingService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.SiteService;
import com.example.ws.microservices.firstmicroservices.serviceImpl.config.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeServices;
    private final EmployeeMappingService employeeMappingService;
    private final SiteService siteService;
    private final ConfigurationService configurationService;


    @GetMapping(path = "/{expertis}")
    public EmployeeDTO getUser(@PathVariable String expertis){

        Optional<EmployeeDTO> getUser = employeeServices.getUsersByExpertis(expertis);
        return getUser.orElse(null);
    }

    @Operation(summary = "Get configuration for registration",
            description = "Fetches configuration data (e.g., agencies, countries, shifts) for a given site.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration fetched successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigurationRegistrationDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/config/{siteName}")
    public ResponseEntity<ConfigurationRegistrationDTO> getAllConfigurationForRegistration(@PathVariable String siteName){
        try {
            ConfigurationRegistrationDTO dto = configurationService.getConfigurationForSite(siteName);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error retrieving configuration for site: {}", siteName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping(path = "/site/{nameSite}")
    public List<EmployeeDTO> getEmployeeBySite(@PathVariable String nameSite){
        return siteService.findEmployeesBySiteName(nameSite);
    }

    @GetMapping(path = "/mapping/{expertis}")
    public EmployeeDTO getUserMapping(@PathVariable String expertis){

        Optional<EmployeeDTO> getUser = employeeMappingService.findByExpertis(expertis);
        return getUser.orElse(null);
    }

    @PostMapping(path = "/site", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeDTO> getListEmployeesMappingForSite(@RequestBody SiteRequestModel siteRequestModel){
        return siteService.findEmployeesByDynamicFilters(siteRequestModel);
    }


    @PostMapping(path = "/mapping", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeDTO> getListEmployeesMapping(@RequestBody List<String> expertisList){
        return employeeMappingService.findByExpertisIn(expertisList);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeDTO> getListEmployees(@RequestBody List<String> expertisList){
        return employeeServices.findEmployeesByExpertisList(expertisList);
    }

}
