package com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.RemoveSupervisionRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.RequestSetEmployeeToSupervisor;

import java.util.List;

public interface SupervisorAssignmentService {

    List<String> addEmployeeAccessForSupervisor(List<RequestSetEmployeeToSupervisor> employees);
    List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor(String expertis);
    List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor();
    void findExpiredAccessesAndDeleteThem();
    void deleteEmployeeAccessForSupervisor(RemoveSupervisionRequest request);
    List<PreviewEmployeeDTO> getEmployeeWithoutSupervisors();
    List<PreviewEmployeeDTO> getSupervisors();
}
