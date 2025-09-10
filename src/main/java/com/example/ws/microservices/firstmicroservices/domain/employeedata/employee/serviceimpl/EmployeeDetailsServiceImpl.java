package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.serviceimpl;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.customError.EmployeeNotFound;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.repository.EmployeeDetailsRepository;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.service.EmployeeDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeDetailsServiceImpl implements EmployeeDetailsService {

    private final EmployeeDetailsRepository employeeDetailsRepository;

    @Override
    public void saveAiEmployee(List<EmployeeDetails> employeeDetails) {
        employeeDetailsRepository.saveAll(employeeDetails);
    }

    @Override
    public EmployeeDetails getAiEmployeeById(Long id) {
        Optional<EmployeeDetails> aiEmployee = employeeDetailsRepository.findByEmployeeId(id);
        if(aiEmployee.isEmpty()){
            throw new EmployeeNotFound("Additional information about employee, not found");
        }else{
            return aiEmployee.get();
        }
    }

    @Override
    public void save(EmployeeDetails employeeDetails) {
        employeeDetailsRepository.save(employeeDetails);
    }
}
