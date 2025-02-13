package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.entity.template.*;
import com.example.ws.microservices.firstmicroservices.mapper.EmployeeMapper;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeMappingRepository;
import com.example.ws.microservices.firstmicroservices.request.CreateEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.response.ConfigurationRegistrationDTO;
import com.example.ws.microservices.firstmicroservices.response.CreateEmployeeResponse;
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
    private final EntityManager entityManager;
    private final SiteService siteService;

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
        List<EmployeeMapping> validEmployees = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        Map<String, Site> validSites = new HashMap<>();

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

                validEmployees.add(employee);
            }

        }

        Set<String> expertisSet = validEmployees.stream()
                .map(EmployeeMapping::getExpertis)
                .collect(Collectors.toSet());

        Set<Short> zalosIdSet = validEmployees.stream()
                .map(EmployeeMapping::getZalosId)
                .collect(Collectors.toSet());

        Set<String> brCodeSet = validEmployees.stream()
                .map(EmployeeMapping::getBrCode)
                .collect(Collectors.toSet());

        List<String> existingExpertisList = employeeMappingRepository.findExistingExpertis(expertisSet, zalosIdSet, brCodeSet);
        Set<String> duplicateExpertis = new HashSet<>(existingExpertisList);

        if (!duplicateExpertis.isEmpty()) {
            for (String dup : duplicateExpertis) {
                String err = String.format("Employee with expertis '%s' already exists.", dup);
                log.error(err);
                errorMessages.add(err);
            }
            validEmployees = validEmployees.stream()
                    .filter(emp -> !duplicateExpertis.contains(emp.getExpertis()))
                    .collect(Collectors.toList());
        }

        if (!validEmployees.isEmpty()) {
            try {
                employeeMappingRepository.saveAll(validEmployees);
            } catch (Exception e) {
                String err = "Error occurred while saving employees: " + e.getMessage();
                log.error(err, e);
                errorMessages.add(err);
            }
        }

        int total = createEmployeeRequests.size();
        int success = validEmployees.size();
        int failed = total - success;

        return new CreateEmployeeResponse(total, success, failed, errorMessages);
    }
}
