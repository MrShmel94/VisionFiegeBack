package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;
import com.example.ws.microservices.firstmicroservices.service.EmployeeMappingService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeServices;
    private final EmployeeMappingService employeeMappingService;
    private final SiteService siteService;


    @GetMapping(path = "/{expertis}")
    public EmployeeDTO getUser(@PathVariable String expertis){

        Optional<EmployeeDTO> getUser = employeeServices.getUsersByExpertis(expertis);
        return getUser.orElse(null);
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
