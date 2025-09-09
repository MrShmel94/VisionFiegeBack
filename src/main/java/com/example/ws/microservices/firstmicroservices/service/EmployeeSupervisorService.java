package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.RemoveSupervisionRequest;
import com.example.ws.microservices.firstmicroservices.request.RequestSetEmployeeToSupervisor;

import java.util.List;

public interface EmployeeSupervisorService {

    List<String> addEmployeeAccessForSupervisor(List<RequestSetEmployeeToSupervisor> employees);
    List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor(String expertis);
    List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor();
    void findExpiredAccessesAndDeleteThem();
    void deleteEmployeeAccessForSupervisor(RemoveSupervisionRequest request);
    List<PreviewEmployeeDTO> getEmployeeWithoutSupervisors();
    List<PreviewEmployeeDTO> getSupervisors();
}
