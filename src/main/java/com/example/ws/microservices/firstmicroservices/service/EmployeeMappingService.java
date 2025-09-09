package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.request.CreateEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.response.CreateEmployeeResponse;
import jakarta.validation.Valid;

import java.io.InputStream;
import java.util.List;

public interface EmployeeMappingService {
    CreateEmployeeResponse createEmployees(@Valid List<CreateEmployeeRequest> createEmployeeRequests);
    List<CreateEmployeeRequest> parse(InputStream inputStream);
    void updateEmployees(CreateEmployeeRequest request);
}
