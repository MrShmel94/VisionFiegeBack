package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department;

import com.example.ws.microservices.firstmicroservices.common.cache.redice.CachingService;

import java.util.List;

public interface DepartmentServiceCache extends CachingService<DepartmentDTO> {
    List<Department> findAll();
    List<DepartmentDTO> getAllFromDB();
}
