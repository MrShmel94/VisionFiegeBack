package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.customError.EmployeeNotFound;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;
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
    public void saveAiEmployee(List<EmployeeDetails> employeeDetails) {
        aiEmployeeRepository.saveAll(employeeDetails);
    }

    @Override
    public EmployeeDetails getAiEmployeeById(Long id) {
        Optional<EmployeeDetails> aiEmployee = aiEmployeeRepository.findByEmployeeId(id);
        if(aiEmployee.isEmpty()){
            throw new EmployeeNotFound("Additional information about employee, not found");
        }else{
            return aiEmployee.get();
        }
    }

    @Override
    public void save(EmployeeDetails employeeDetails) {
        aiEmployeeRepository.save(employeeDetails);
    }
}
