package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.customError.EmployeeNotFound;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.aiemployee.AiEmployee;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.AiEmployeeRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.AiEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiEmployeeServiceImpl implements AiEmployeeService {

    private final AiEmployeeRepository aiEmployeeRepository;

    @Override
    public void saveAiEmployee(List<AiEmployee> aiEmployee) {
        aiEmployeeRepository.saveAll(aiEmployee);
    }

    @Override
    public AiEmployee getAiEmployeeById(Long id) {
        Optional<AiEmployee> aiEmployee = aiEmployeeRepository.findByEmployeeId(id);
        if(aiEmployee.isEmpty()){
            throw new EmployeeNotFound("Additional information about employee, not found");
        }else{
            return aiEmployee.get();
        }
    }

    @Override
    public void save(AiEmployee aiEmployee) {
        aiEmployeeRepository.save(aiEmployee);
    }
}
