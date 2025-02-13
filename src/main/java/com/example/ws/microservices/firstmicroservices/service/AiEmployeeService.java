package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.entity.AiEmployee;

import java.util.List;

public interface AiEmployeeService {
     void saveAiEmployee(List<AiEmployee> aiEmployee);
}
