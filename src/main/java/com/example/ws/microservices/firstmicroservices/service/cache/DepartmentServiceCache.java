package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Department;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface DepartmentServiceCache extends CachingService<DepartmentDTO> {
    List<Department> findAll();
    List<DepartmentDTO> getAllFromDB();
}
