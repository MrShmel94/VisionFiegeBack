package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeRepository;
import com.example.ws.microservices.firstmicroservices.response.PaginatedResponse;
import com.example.ws.microservices.firstmicroservices.secure.aspects.AccessControl;
import com.example.ws.microservices.firstmicroservices.secure.aspects.MaskField;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RedisCacheService redisCacheService;

    @Value("${pagination.max-page-size}")
    private int PAGE_SIZE_MAX;

    @Override
    @AccessControl(
            minWeight = 35
    )
    @MaskField
    public EmployeeDTO getUsersByExpertis(String expertis) {
        Optional<EmployeeDTO> dto = employeeRepository.findEmployeeByExpertis(expertis);
        if(dto.isPresent()) {
            return dto.get();
        }else{
            throw new UsernameNotFoundException(
                    String.format("User %s not found", expertis));
        }
    }

    @Override
    public Optional<EmployeeDTO> getUsersByExpertisForRegistration(String expertis) {
        return employeeRepository.findEmployeeByExpertis(expertis);
    }

    @Override
    public PaginatedResponse<EmployeeFullInformationDTO> getEmployeesByExpertisList(List<String> expertisList, Pageable pageable) {

        Pageable adjustedPageable = pageable.getPageSize() > PAGE_SIZE_MAX
                ? PageRequest.of(pageable.getPageNumber(), PAGE_SIZE_MAX, pageable.getSort())
                : pageable;

        List<String> uniqueExpertis = new ArrayList<>(new LinkedHashSet<>(expertisList));

        if (uniqueExpertis.size() <= adjustedPageable.getPageSize()) {
            Map<String, EmployeeFullInformationDTO> cachedEmployees = redisCacheService.getMultiFromCacheAsMap(uniqueExpertis, EmployeeFullInformationDTO.class);

            List<String> missingExpertis = uniqueExpertis.stream()
                    .filter(expertis -> !cachedEmployees.containsKey(expertis))
                    .toList();

            List<EmployeeFullInformationDTO> dbEmployees = missingExpertis.isEmpty() ? List.of() :
                    employeeRepository.findEmployeeFullInformationByExpertisList(missingExpertis);

            dbEmployees.forEach(employee -> redisCacheService.saveToCache("employee:" + employee.getExpertis(), employee));

            List<EmployeeFullInformationDTO> allResults = Stream.concat(
                    cachedEmployees.values().stream(),
                    dbEmployees.stream()
            ).toList();

            return PaginatedResponse.<EmployeeFullInformationDTO>builder()
                    .data(allResults)
                    .missingItems(missingExpertis)
                    .totalItems(allResults.size())
                    .totalPages(1)
                    .currentPage(0)
                    .pageSize(allResults.size())
                    .build();
        }

        Page<EmployeeFullInformationDTO> page = employeeRepository.findEmployeeFullInformationByExpertisListPageable(uniqueExpertis, adjustedPageable);
        List<EmployeeFullInformationDTO> employees = page.getContent();

        employees.forEach(employee -> redisCacheService.saveToCache("employee:" + employee.getExpertis(), employee));

        List<String> foundExpertises = employees.stream()
                .map(EmployeeFullInformationDTO::getExpertis)
                .toList();

        List<String> missingExpertises = uniqueExpertis.stream()
                .filter(expertis -> !foundExpertises.contains(expertis))
                .toList();

        return PaginatedResponse.<EmployeeFullInformationDTO>builder()
                .data(employees)
                .missingItems(missingExpertises)
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(adjustedPageable.getPageNumber())
                .pageSize(adjustedPageable.getPageSize())
                .build();
    }

    @Override
    public Map<String, EmployeeFullInformationDTO> getEmployeeFullDTO(List<String> expertis){
        Map<String, EmployeeFullInformationDTO> map = redisCacheService.getEmployeeFullMapping(expertis, EmployeeFullInformationDTO.class);
        if(expertis.size() != map.size()){
            List<String> notContainExpertis = expertis.stream().filter(exp -> !map.containsKey(exp)).toList();
            List<EmployeeFullInformationDTO> restEmployee = employeeRepository.findEmployeeFullInformationByExpertisList(notContainExpertis);
            restEmployee.forEach(employee -> {
                redisCacheService.saveMapping("userFullMapping", employee.getExpertis(), employee);
                map.put(employee.getExpertis(), employee);
            });

        }
        return map;
    }

}
