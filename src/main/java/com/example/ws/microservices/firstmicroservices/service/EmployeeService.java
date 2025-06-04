package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.Employee;
import com.example.ws.microservices.firstmicroservices.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmployeeService {

    EmployeeDTO getUsersByExpertis(String expertis);

    List<String> getAllExpertis();

    Optional<EmployeeDTO> getUsersByExpertisForRegistration(String expertis);

    PaginatedResponse<EmployeeFullInformationDTO> getEmployeesByExpertisList(
            List<String> expertisList, Pageable pageable);

    Map<String, EmployeeFullInformationDTO> getEmployeeFullDTO(List<String> expertis);
    EmployeeFullInformationDTO getEmployeeFullInformation(String expertis);

    Employee getEmployeeByExpertis(String expertis);

    void save(Employee employee);

    List<EmployeeFullInformationDTO> searchByQuery(String query);

    void removeEmployeeFromRedis(String expertis);

    List<PreviewEmployeeDTO> getAllEmployeesWithPlannedShiftMatching(LocalDate dateTraining, LocalTime startTime, LocalTime endTime, List<Integer> positionsIds, String nameDoc);
}
