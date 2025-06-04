package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.*;
import com.example.ws.microservices.firstmicroservices.response.ConfigurationRegistrationDTO;
import com.example.ws.microservices.firstmicroservices.response.CreateEmployeeResponse;
import com.example.ws.microservices.firstmicroservices.service.EmployeeMappingService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeSupervisorService;
import com.example.ws.microservices.firstmicroservices.service.SiteService;
import com.example.ws.microservices.firstmicroservices.serviceImpl.config.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeServices;
    private final EmployeeSupervisorService employeeSupervisorService;
    private final EmployeeMappingService employeeMappingService;
    private final SiteService siteService;
    private final ConfigurationService configurationService;
    private final Validator validator;


    @GetMapping(path = "/{expertis}")
    public EmployeeDTO getUser(@PathVariable String expertis){
        return employeeServices.getUsersByExpertis(expertis);
    }

    @Operation(summary = "Get configuration for registration",
            description = "Fetches configuration data (e.g., agencies, countries, shifts) for a given site.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration fetched successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigurationRegistrationDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/config")
    public ResponseEntity<ConfigurationRegistrationDTO> getAllConfigurationForRegistration(){
        try {
            ConfigurationRegistrationDTO dto = configurationService.getConfigurationForSite();
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error retrieving configuration for site", e);
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

    @PostMapping(path = "/createEmployees", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateEmployeeResponse> createEmployees(@Valid @RequestBody CreateEmployeeRequestList createEmployeeRequests){
        CreateEmployeeResponse response = employeeMappingService.createEmployees(createEmployeeRequests.getEmployees());
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/updateEmployee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEmployees(@Valid @RequestBody CreateEmployeeRequest createEmployeeRequests){
        employeeMappingService.updateEmployees(createEmployeeRequests);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/searchQuery/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeeFullInformationDTO>> updateEmployees(@PathVariable("query") String query){
        List<EmployeeFullInformationDTO> allEmployee = employeeServices.searchByQuery(query);
        return ResponseEntity.ok(allEmployee);
    }

    @PostMapping(path = "/uploadEmployees", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadEmployees(@RequestParam("file") MultipartFile file) {
        List<CreateEmployeeRequest> employeeRequests;
        try {
            employeeRequests = employeeMappingService.parse(file.getInputStream());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error parsing Excel file: " + e.getMessage());
        }

        CreateEmployeeRequestList requestList = new CreateEmployeeRequestList();
        requestList.setEmployees(employeeRequests);

        Set<ConstraintViolation<CreateEmployeeRequestList>> violations = validator.validate(requestList);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<CreateEmployeeRequestList> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        CreateEmployeeResponse response = employeeMappingService.createEmployees(employeeRequests);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of employees without a supervisor for the current user's site.
     *
     * @return ResponseEntity with a list of PreviewEmployeeDTO representing employees without supervisors.
     */
    @Operation(summary = "Get Employees Without Supervisor",
            description = "Retrieves the list of employees who do not have a supervisor assigned for the current user's site.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PreviewEmployeeDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(path = "/getEmployeeWithoutSupervisor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PreviewEmployeeDTO>> getListEmployeesWithoutSupervisor(){
        log.info("Controller: Fetching employees without supervisor.");
        List<PreviewEmployeeDTO> employees = employeeSupervisorService.getEmployeeWithoutSupervisors();
        log.info("Controller: Found {} employees without supervisor.", employees.size());
        return ResponseEntity.ok(employees);
    }

    /**
     * Retrieves a list of supervisors for the current user's site.
     *
     * @return ResponseEntity with a list of PreviewEmployeeDTO representing supervisors.
     */
    @Operation(summary = "Get Supervisors",
            description = "Retrieves the list of supervisors for the current user's site.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved supervisors",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PreviewEmployeeDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(path = "/getSupervisor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PreviewEmployeeDTO>> getListSupervisor() {
        log.info("Controller: Fetching supervisors.");
        List<PreviewEmployeeDTO> supervisors = employeeSupervisorService.getSupervisors();
        log.info("Controller: Found {} supervisors.", supervisors.size());
        return ResponseEntity.ok(supervisors);
    }

    /**
     * Retrieves full employee information for a given supervisor.
     *
     * @param expertis The supervisor's expertis.
     * @return ResponseEntity with a list of EmployeeFullInformationDTO for the given supervisor.
     */
    @Operation(summary = "Get Employees By Supervisor",
            description = "Retrieves the full employee information for employees under the supervisor identified by expertis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee details",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EmployeeFullInformationDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(path = "/getEmployeeBySupervisor/{expertis}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeeFullInformationDTO>> getListEmployeeBySupervisor(
            @PathVariable("expertis") String expertis) {
        List<EmployeeFullInformationDTO> employees = employeeSupervisorService.getAllEmployeeBySupervisor(expertis);
        return ResponseEntity.ok(employees);
    }

    @GetMapping(path = "/getEmployeeBySupervisor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeeFullInformationDTO>> getListEmployeeBySupervisor() {
        List<EmployeeFullInformationDTO> employees = employeeSupervisorService.getAllEmployeeBySupervisor();
        return ResponseEntity.ok(employees);
    }

    @GetMapping(path = "/getEmployeeInformation/{expertis}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeFullInformationDTO> getEmployeeInformation(@PathVariable("expertis") String expertis) {
        return ResponseEntity.ok(employeeServices.getEmployeeFullInformation(expertis));
    }

    /**
     * Assigns employees to a supervisor.
     *
     * @param setEmployeeToSupervisor List of mapping requests containing supervisor and employee expertis with validity dates.
     * @return ResponseEntity containing a list of employee expertis that were successfully added.
     */
    @Operation(summary = "Assign Employees to Supervisor",
            description = "Assigns a set of employees to a supervisor using the provided mapping requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully assigned employees",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping(path = "/setEmployeeToSupervisor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> setEmployeeToSupervisor(
            @RequestBody List<RequestSetEmployeeToSupervisor> setEmployeeToSupervisor) {
        List<String> result = employeeSupervisorService.addEmployeeAccessForSupervisor(setEmployeeToSupervisor);
        return ResponseEntity.ok(result);
    }

    /**
     * Removes employee assignments from a supervisor.
     *
     * @param removeSupervisionRequest List of mapping requests for employees to remove from a supervisor.
     * @return ResponseEntity with HTTP status OK if deletion is successful.
     */
    @Operation(summary = "Remove Employees from Supervisor",
            description = "Deletes the mapping between employees and a supervisor based on the provided mapping requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed employee access"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping(path = "/deleteEmployeeToSupervisor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteEmployeeToSupervisor(
            @RequestBody RemoveSupervisionRequest removeSupervisionRequest) {
        employeeSupervisorService.deleteEmployeeAccessForSupervisor(removeSupervisionRequest);
        return ResponseEntity.ok().build();
    }

}
