package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmployeeService {

    EmployeeDTO getUsersByExpertis(String expertis);

    Optional<EmployeeDTO> getUsersByExpertisForRegistration(String expertis);

    PaginatedResponse<EmployeeFullInformationDTO> getEmployeesByExpertisList(
            List<String> expertisList, Pageable pageable);

    Map<String, EmployeeFullInformationDTO> getEmployeeFullDTO(List<String> expertis);
}
