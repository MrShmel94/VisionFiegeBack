package com.example.ws.microservices.firstmicroservices.oldstructure.service.vision;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.DepartmentDTO;

import java.util.List;

public interface DepartmentService {

    List<DepartmentDTO> getDepartments();
    List<DepartmentDTO> getDepartmentsBySupervisorSite(String userId);
    List<DepartmentDTO> getDepartmentsBySupervisorSite();
}
