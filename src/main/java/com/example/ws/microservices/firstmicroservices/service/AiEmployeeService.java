package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.entity.vision.AiEmployee;

import java.util.List;

public interface AiEmployeeService {
     void saveAiEmployee(List<AiEmployee> aiEmployee);
     AiEmployee getAiEmployeeById(Long id);
     void save(AiEmployee aiEmployee);
}
