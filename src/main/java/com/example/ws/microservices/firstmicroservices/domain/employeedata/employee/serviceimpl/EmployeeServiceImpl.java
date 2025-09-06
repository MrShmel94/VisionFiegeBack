package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.serviceimpl;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.customError.EmployeeNotFound;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.Employee;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.repository.EmployeeRepository;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.PaginatedResponse;
import com.example.ws.microservices.firstmicroservices.common.security.aspects.AccessControl;
import com.example.ws.microservices.firstmicroservices.common.security.aspects.MaskField;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RedisService redisService;

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
    public List<String> getAllExpertis() {
        return redisService.getFromCache("allExpertis", new TypeReference<List<String>>() {})
                .orElseGet(() -> {
                    List<String> allExpertis = employeeRepository.getAllExpertis();
                    redisService.saveToCacheWithTTL("allExpertis", allExpertis, Duration.ofHours(24));
                    return allExpertis;
                });
    }

    @Override
    public Optional<EmployeeDTO> getUsersByExpertisForRegistration(String expertis) {
        return employeeRepository.findEmployeeByExpertis(expertis);
    }

    @Override
    public Map<String, EmployeeFullInformationDTO> getEmployeeFullDTO(List<String> expertis){
        Map<String, EmployeeFullInformationDTO> map = redisService.getEmployeeFullMapping(expertis, EmployeeFullInformationDTO.class);

        if(expertis.size() != map.size()){
            List<String> notContainExpertis = expertis.stream().filter(exp -> !map.containsKey(exp)).toList();
            List<EmployeeFullInformationDTO> restEmployee = employeeRepository.findEmployeeFullInformationByExpertisList(notContainExpertis);

            Map<String, EmployeeFullInformationDTO> toSave = restEmployee.stream()
                    .collect(Collectors.toMap(EmployeeFullInformationDTO::getExpertis, Function.identity()));

            redisService.saveAllMapping("userFullMapping", toSave);
            map.putAll(toSave);

        }
        return map;
    }

    @Override
    @MaskField
    public EmployeeFullInformationDTO getEmployeeFullInformation(String expertis){
        Map<String, EmployeeFullInformationDTO> mapResul = getEmployeeFullDTO(List.of(expertis));
        if(!mapResul.containsKey(expertis)){
            throw new EmployeeNotFound("Employee with expertis " + expertis + " not found");
        }else{
            return mapResul.get(expertis);
        }
    }

    @Override
    public Employee getEmployeeByExpertis(String expertis) {
        Optional<Employee> employee = employeeRepository.findByExpertis(expertis);
        if(employee.isEmpty()){
            throw new EmployeeNotFound("Employee with expertis " + expertis + " not found");
        }else{
            return employee.get();
        }
    }

    @Override
    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeFullInformationDTO> searchByQuery(String query) {

        List<EmployeeFullInformationDTO> dto = new ArrayList<>();

        if (query.contains(" ")) {
            String[] parts = query.trim().split("\\s+", 2);
            String part1 = parts[0];
            String part2 = parts[1];

            dto = employeeRepository.searchByFullNameVariants(part1, part2);
        } else {
            dto = employeeRepository.searchByAnyField(query);
        }

        if(!dto.isEmpty()){
            Map<String, EmployeeFullInformationDTO> toSave = dto.stream().collect(Collectors.toMap(EmployeeFullInformationDTO::getExpertis, Function.identity()));
            redisService.saveAllMapping("userFullMapping", toSave);
        }

        return dto;
    }

    @Override
    public void removeEmployeeFromRedis(String expertis) {
        redisService.removeEmployeeFromMapping(expertis);
    }

    @Override
    public List<PreviewEmployeeDTO> getAllEmployeesWithPlannedShiftMatching(LocalDate dateTraining, LocalTime startTime, LocalTime endTime, List<Integer> positionsIds, String nameDoc) {
        return employeeRepository.findAllEmployeesWithPlannedShiftMatching(dateTraining, startTime, endTime, positionsIds, nameDoc);
    }

    @Override
    public PaginatedResponse<EmployeeFullInformationDTO> getEmployeesByExpertisList(List<String> expertisList, Pageable pageable) {

        Pageable adjustedPageable = pageable.getPageSize() > PAGE_SIZE_MAX
                ? PageRequest.of(pageable.getPageNumber(), PAGE_SIZE_MAX, pageable.getSort())
                : pageable;

        List<String> uniqueExpertis = new ArrayList<>(new LinkedHashSet<>(expertisList));

        if (uniqueExpertis.size() <= adjustedPageable.getPageSize()) {
            Map<String, EmployeeFullInformationDTO> cachedEmployees = redisService.getMultiFromCacheAsMap(uniqueExpertis, EmployeeFullInformationDTO.class);

            List<String> missingExpertis = uniqueExpertis.stream()
                    .filter(expertis -> !cachedEmployees.containsKey(expertis))
                    .toList();

            List<EmployeeFullInformationDTO> dbEmployees = missingExpertis.isEmpty() ? List.of() :
                    employeeRepository.findEmployeeFullInformationByExpertisList(missingExpertis);

            dbEmployees.forEach(employee -> redisService.saveToCache("employee:" + employee.getExpertis(), employee));

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

        employees.forEach(employee -> redisService.saveToCache("employee:" + employee.getExpertis(), employee));

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

}
