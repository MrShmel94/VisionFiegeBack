package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.RequestSetEmployeeToSupervisor;

import java.util.List;

public interface EmployeeSupervisorService {

    List<String> addEmployeeAccessForSupervisor(List<RequestSetEmployeeToSupervisor> employees);
    List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor(String expertis);
    void findExpiredAccessesAndDeleteThem();
    void deleteEmployeeAccessForSupervisor(List<RequestSetEmployeeToSupervisor> employees);
    List<PreviewEmployeeDTO> getEmployeeWithoutSupervisors();
    List<PreviewEmployeeDTO> getSupervisors();
}
