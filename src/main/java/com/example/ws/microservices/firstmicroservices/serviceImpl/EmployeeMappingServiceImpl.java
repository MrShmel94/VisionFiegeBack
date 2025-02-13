package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.entity.AiEmployee;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.entity.template.*;
import com.example.ws.microservices.firstmicroservices.mapper.EmployeeMapper;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeMappingRepository;
import com.example.ws.microservices.firstmicroservices.request.CreateEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.request.EmployeeData;
import com.example.ws.microservices.firstmicroservices.response.ConfigurationRegistrationDTO;
import com.example.ws.microservices.firstmicroservices.response.CreateEmployeeResponse;
import com.example.ws.microservices.firstmicroservices.service.AiEmployeeService;
import com.example.ws.microservices.firstmicroservices.service.EmployeeMappingService;
import com.example.ws.microservices.firstmicroservices.service.SiteService;
import com.example.ws.microservices.firstmicroservices.serviceImpl.config.ConfigurationService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeMappingServiceImpl implements EmployeeMappingService {

    private final EmployeeMappingRepository employeeMappingRepository;
    private final ConfigurationService configurationService;
    private final AiEmployeeService aiEmployeeService;
    private final EntityManager entityManager;
    private final SiteService siteService;

    /**
     * Creates new employees based on the provided requests.
     * <p>
     * The method retrieves the configuration for the given site (using a cache to avoid multiple lookups),
     * validates that all necessary related entities (department, shift, team, country, position, agency) are present,
     * and maps the request DTOs to EmployeeMapping entities using MapStruct.
     *
     * @param createEmployeeRequests a list of employee creation requests
     * @return response with counts and error details
     */
    @Override
    @Transactional
    public CreateEmployeeResponse createEmployees(List<CreateEmployeeRequest> createEmployeeRequests) {
        Map<String, ConfigurationRegistrationDTO> configMap = new HashMap<>();
        Map<String, Site> validSites = new HashMap<>();

        List<EmployeeData> employeeDataList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (CreateEmployeeRequest request : createEmployeeRequests) {
            ConfigurationRegistrationDTO config = configMap.computeIfAbsent(
                    request.getSite(),
                    configurationService::getConfigurationForSite
            );

            if (config == null) {
                String err = String.format("Configuration not found for site: %s", request.getSite());
                log.error(err);
                errorMessages.add(err);
            } else {
                EmployeeMapping employee = EmployeeMapper.INSTANCE.toEmployeeMapping(request);
                AiEmployee aiEmployee = EmployeeMapper.INSTANCE.toAiEmployee(request);

                Site site = validSites.computeIfAbsent(
                        request.getSite(),
                        key -> siteService.findSiteByName(key).orElse(null)
                );
                if (site == null) {
                    String err = String.format("Site entity not found in configuration for site: %s, for employee: %s", request.getSite(), request.getExpertis());
                    errorMessages.add(err);
                    log.error(err);
                    continue;
                }
                employee.setSite(site);

                Department departmentEntity = config.getDepartments().stream()
                        .filter(d -> d.getName().equalsIgnoreCase(request.getDepartment()))
                        .findFirst()
                        .map(deptDTO -> entityManager.getReference(Department.class, deptDTO.getId()))
                        .orElse(null);

                if (departmentEntity == null) {
                    String err = String.format("Department entity not found for site: %s, department: %s, for employee: %s", request.getSite(),  request.getDepartment(), request.getExpertis());
                    log.error(err);
                    errorMessages.add(err);
                    continue;
                }
                employee.setDepartment(departmentEntity);

                Shift shiftEntity = config.getShifts().stream()
                        .filter(s -> s.getName().equalsIgnoreCase(request.getShift()))
                        .findFirst()
                        .map(shiftDTO -> entityManager.getReference(Shift.class, shiftDTO.getId()))
                        .orElse(null);
                if (shiftEntity == null) {
                    String err = String.format("Shift entity not found for site: %s, shift: %s, for employee: %s", request.getSite(),  request.getShift(),  request.getExpertis());
                    log.error(err);
                    errorMessages.add(err);
                    continue;
                }
                employee.setShift(shiftEntity);

                Team teamEntity = config.getTeams().stream()
                        .filter(t -> t.getName().equalsIgnoreCase(request.getTeam()))
                        .findFirst()
                        .map(teamDTO -> entityManager.getReference(Team.class, teamDTO.getId()))
                        .orElse(null);

                if (teamEntity == null) {
                    String err = String.format("Team entity not found for site: %s, team: %s, for employee: %s", request.getSite(),  request.getTeam(),  request.getExpertis());
                    log.error(err);
                    errorMessages.add(err);
                    continue;
                }
                employee.setTeam(teamEntity);

                Country countryEntity = config.getCountries().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(request.getCountry()))
                        .findFirst()
                        .map(countryDTO -> entityManager.getReference(Country.class, countryDTO.getId()))
                        .orElse(null);
                if (countryEntity == null) {
                    String err = String.format("Country entity not found for site: %s, country: %s, for employee: %s", request.getSite(),  request.getCountry(),  request.getExpertis());
                    log.error(err);
                    errorMessages.add(err);
                    continue;
                }
                employee.setCountry(countryEntity);

                Position positionEntity = config.getPositions().stream()
                        .filter(p -> p.getName().equalsIgnoreCase(request.getPosition()))
                        .findFirst()
                        .map(positionDTO -> entityManager.getReference(Position.class, positionDTO.getId()))
                        .orElse(null);
                if (positionEntity == null) {
                    String err = String.format("Position entity not found for site: %s, position: %s, for employee: %s", request.getSite(),  request.getPosition(),  request.getExpertis());
                    log.error(err);
                    errorMessages.add(err);
                    continue;
                }
                employee.setPosition(positionEntity);

                Agency agencyEntity = config.getAgencies().stream()
                        .filter(a -> a.getName().equalsIgnoreCase(request.getAgency()))
                        .findFirst()
                        .map(agencyDTO -> entityManager.getReference(Agency.class, agencyDTO.getId()))
                        .orElse(null);
                if (agencyEntity == null) {
                    String err = String.format("Agency entity not found for site: %s, agency: %s, for employee: %s", request.getSite(),  request.getAgency(),  request.getExpertis());
                    log.error(err);
                    errorMessages.add(err);
                    continue;
                }
                employee.setAgency(agencyEntity);
                aiEmployee.setEmployee(employee);

                employeeDataList.add(new EmployeeData(employee, aiEmployee));
            }

        }

        Set<String> expertisSet = new HashSet<>();
        Set<Short> zalosIdSet = new HashSet<>();
        Set<String> brCodeSet = new HashSet<>();

        for (EmployeeData data : employeeDataList) {
            EmployeeMapping emp = data.getEmployee();
            expertisSet.add(emp.getExpertis());
            if (emp.getZalosId() != null) {
                zalosIdSet.add(emp.getZalosId());
            }
            if (emp.getBrCode() != null) {
                brCodeSet.add(emp.getBrCode());
            }
        }

        List<Object[]> duplicates = employeeMappingRepository.findExistingDuplicates(expertisSet, zalosIdSet, brCodeSet);

        Set<String> duplicateExpertis = new HashSet<>();
        Set<Short> duplicateZalosId = new HashSet<>();
        Set<String> duplicateBrCode = new HashSet<>();

        for (Object[] row : duplicates) {
            if (row[0] != null) {
                duplicateExpertis.add((String) row[0]);
            }
            if (row[1] != null) {
                duplicateZalosId.add((Short) row[1]);
            }
            if (row[2] != null) {
                duplicateBrCode.add((String) row[2]);
            }
        }

        List<EmployeeData> finalEmployeeData = employeeDataList.stream()
                .filter(data -> {
                    EmployeeMapping emp = data.getEmployee();
                    boolean isDuplicate = duplicateExpertis.contains(emp.getExpertis())
                            || (emp.getZalosId() != null && duplicateZalosId.contains(emp.getZalosId()))
                            || (emp.getBrCode() != null && duplicateBrCode.contains(emp.getBrCode()));
                    if (isDuplicate) {
                        String err = String.format("Employee with expertis '%s', zalosId '%s' or brCode '%s' already exists.",
                                emp.getExpertis(), emp.getZalosId(), emp.getBrCode());
                        log.error(err);
                        errorMessages.add(err);
                    }
                    return !isDuplicate;
                })
                .toList();

        List<EmployeeMapping> finalEmployees = finalEmployeeData.stream()
                .map(EmployeeData::getEmployee)
                .collect(Collectors.toList());
        List<AiEmployee> finalAdditionalInfo = finalEmployeeData.stream()
                .map(EmployeeData::getAdditional)
                .collect(Collectors.toList());

        if (!finalEmployees.isEmpty()) {
            try {
                employeeMappingRepository.saveAll(finalEmployees);
                aiEmployeeService.saveAiEmployee(finalAdditionalInfo);
            } catch (Exception e) {
                String err = "Error occurred while saving employees: " + e.getMessage();
                log.error(err, e);
                errorMessages.add(err);
            }
        }

        int total = createEmployeeRequests.size();
        int success = finalEmployees.size();
        int failed = total - success;

        return new CreateEmployeeResponse(total, success, failed, errorMessages);
    }
}
