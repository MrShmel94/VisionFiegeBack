package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeRepository;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Optional<EmployeeDTO> getUsersByExpertis(String expertis) {
        return employeeRepository.findEmployeeByExpertis(expertis);
    }

    @Override
    public List<EmployeeDTO> findEmployeesByExpertisList(List<String> expertisList) {
        return employeeRepository.findEmployeesByExpertisList(expertisList);
    }


}
