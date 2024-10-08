package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;

import java.util.List;
import java.util.Optional;

public interface EmployeeMappingService {

    Optional<EmployeeDTO> findByExpertis(String expertis);
    List<EmployeeDTO> findByExpertisIn(List<String> expertisList);

}
