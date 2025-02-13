package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.CreateEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.request.CreateEmployeeRequestList;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;
import com.example.ws.microservices.firstmicroservices.response.ConfigurationRegistrationDTO;
import com.example.ws.microservices.firstmicroservices.response.CreateEmployeeResponse;
import com.example.ws.microservices.firstmicroservices.service.EmployeeMappingService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.SiteService;
import com.example.ws.microservices.firstmicroservices.serviceImpl.config.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    public ResponseEntity<ConfigurationRegistrationDTO> getAllConfigurationForRegistration(@Valid @PathVariable String siteName){
        try {
            ConfigurationRegistrationDTO dto = configurationService.getConfigurationForSite(siteName);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error retrieving configuration for site: {}", siteName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(
            summary = "Retrieve employees by site filters",
            description = "Fetches a list of employees using dynamic criteria. The 'nameSite' field is required, "
                    + "while additional fields (nameDepartment, nameShift, nameTeam, namePosition, nameAgency, nameCountry) "
                    + "are optional and can accept multiple values. If an optional filter is not provided or empty, it is ignored."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee list",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EmployeeDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeDTO> getListEmployeesMappingForSite(@Valid @RequestBody SiteRequestModel siteRequestModel){
        return siteService.findEmployeesByDynamicFilters(siteRequestModel);
    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateEmployeeResponse> createEmployees(@Valid @RequestBody CreateEmployeeRequestList createEmployeeRequests){
        CreateEmployeeResponse response = employeeMappingService.createEmployees(createEmployeeRequests.getEmployees());
        return ResponseEntity.ok(response);
    }

}
