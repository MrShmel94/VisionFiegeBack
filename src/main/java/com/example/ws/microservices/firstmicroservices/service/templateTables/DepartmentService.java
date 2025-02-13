package com.example.ws.microservices.firstmicroservices.service.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.CountryDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Country;
import com.example.ws.microservices.firstmicroservices.entity.template.Department;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface DepartmentService extends CachingService<DepartmentDTO> {
    List<Department> findAll();
    List<DepartmentDTO> getAllFromDB();
}
