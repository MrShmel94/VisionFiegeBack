package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.entity.AiEmployee;
import com.example.ws.microservices.firstmicroservices.repository.AiEmployeeRepository;
import com.example.ws.microservices.firstmicroservices.service.AiEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiEmployeeServiceImpl implements AiEmployeeService {

    private final AiEmployeeRepository aiEmployeeRepository;

    @Override
    public void saveAiEmployee(List<AiEmployee> aiEmployee) {
        aiEmployeeRepository.saveAll(aiEmployee);
    }
}
