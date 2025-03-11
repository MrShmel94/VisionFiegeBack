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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private final List<String> EXPECTED_HEADERS = List.of(
            "expertis", "firstname", "lastname",
            "sex", "site", "shift", "department", "team", "country",
            "position", "agency", "datestartcontract", "datefinishcontract"
    );

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    @Override
    public List<CreateEmployeeRequest> parse(InputStream inputStream) {
        List<CreateEmployeeRequest> employees = new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("No workbook create in the Excel file.");
        }
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new IllegalArgumentException("No sheet found in the Excel file.");
        }

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("No header row found in the Excel file.");
        }

        Map<Integer, String> headers = new HashMap<>();
        for (Cell cell : headerRow) {
            headers.put(cell.getColumnIndex(), cell.getStringCellValue().trim().toLowerCase());
        }

        List<String> missingHeaders = new ArrayList<>();

        for (String expected : EXPECTED_HEADERS) {
            if (!headers.containsValue(expected)) {
                missingHeaders.add(expected);
            }
        }
        if (!missingHeaders.isEmpty()) {
            throw new IllegalArgumentException("Missing expected headers: " + missingHeaders);
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            CreateEmployeeRequest request = new CreateEmployeeRequest();
            for (Cell cell : row) {
                String header = headers.get(cell.getColumnIndex());
                DataFormatter dataFormatter = new DataFormatter();
                String value = dataFormatter.formatCellValue(cell).trim();

                switch (header) {
                    case "expertis":
                        request.setExpertis(value);
                        break;
                    case "zalosid":
                        request.setZalosId(value.isEmpty() ? null : Short.valueOf(value));
                        break;
                    case "brcode":
                        request.setBrCode(value);
                        break;
                    case "firstname":
                        request.setFirstName(value);
                        break;
                    case "lastname":
                        request.setLastName(value);
                        break;
                    case "sex":
                        request.setSex(value);
                        break;
                    case "site":
                        request.setSite(value);
                        break;
                    case "shift":
                        request.setShift(value);
                        break;
                    case "department":
                        request.setDepartment(value);
                        break;
                    case "team":
                        request.setTeam(value);
                        break;
                    case "country":
                        request.setCountry(value);
                        break;
                    case "position":
                        request.setPosition(value);
                        break;
                    case "agency":
                        request.setAgency(value);
                        break;
                    case "datestartcontract":
                        request.setDateStartContract(LocalDate.parse(value, formatter));
                        break;
                    case "datefinishcontract":
                        request.setDateFinishContract(LocalDate.parse(value, formatter));
                        break;
                    default:
                        break;
                }
            }
            employees.add(request);
        }
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }
}


