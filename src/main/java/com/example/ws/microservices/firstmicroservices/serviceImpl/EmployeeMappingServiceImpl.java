package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeMappingRepository;
import com.example.ws.microservices.firstmicroservices.service.EmployeeMappingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeMappingServiceImpl implements EmployeeMappingService {

    private final EmployeeMappingRepository employeeMappingRepository;

    @Override
    public Optional<EmployeeDTO> findByExpertis(String expertis) {
        return employeeMappingRepository.findByExpertis(expertis)
                .map(employee -> EmployeeDTO.builder()
                        .id(employee.getId())
                        .expertis(employee.getExpertis())
                        .zalosId(employee.getZalosId())
                        .brCode(employee.getBrCode())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .isWork(employee.getIsWork())
                        .sex(employee.getSex())
                        .siteName(employee.getSite().getName())
                        .shiftName(employee.getShift().getName())
                        .departmentName(employee.getDepartment().getName())
                        .teamName(employee.getTeam().getName())
                        .positionName(employee.getPosition().getName())
                        .agencyName(employee.getAgency().getName())
                        .build());
    }

    @Override
    public List<EmployeeDTO> findByExpertisIn(List<String> expertisList) {
        List<EmployeeMapping> employees = employeeMappingRepository.findByExpertisIn(expertisList);

        return employees.stream().map(employee -> EmployeeDTO.builder()
                .id(employee.getId())
                .expertis(employee.getExpertis())
                .zalosId(employee.getZalosId())
                .brCode(employee.getBrCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .isWork(employee.getIsWork())
                .sex(employee.getSex())
                .siteName(employee.getSite().getName())
                .shiftName(employee.getShift().getName())
                .departmentName(employee.getDepartment().getName())
                .teamName(employee.getTeam().getName())
                .positionName(employee.getPosition().getName())
                .agencyName(employee.getAgency().getName())
                .build()
        ).toList();
    }
}
