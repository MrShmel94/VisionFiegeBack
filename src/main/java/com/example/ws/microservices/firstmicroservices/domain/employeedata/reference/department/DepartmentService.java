package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department;

import java.util.List;

public interface DepartmentService {

    List<DepartmentDTO> getDepartments();
    List<DepartmentDTO> getDepartmentsBySupervisorSite(String userId);
    List<DepartmentDTO> getDepartmentsBySupervisorSite();
}
