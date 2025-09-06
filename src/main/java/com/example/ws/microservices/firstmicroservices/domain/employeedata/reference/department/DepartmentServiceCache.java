package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisPreLoader;

import java.util.List;

public interface DepartmentServiceCache extends RedisPreLoader<DepartmentDTO> {
    List<Department> findAll();
    List<DepartmentDTO> getAllFromDB();
}
