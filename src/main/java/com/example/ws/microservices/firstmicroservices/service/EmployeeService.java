package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<EmployeeDTO> getUsersByExpertis(String expertis);
    List<EmployeeDTO> findEmployeesByExpertisList(List<String> expertisList);
}
