package com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.RemoveSupervisionRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.RequestSetEmployeeToSupervisor;
import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.common.security.annotations.MaskField;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupervisorAssignmentServiceImpl implements SupervisorAssignmentService {

    private final SupervisorAssignmentRepository repository;
    private final EmployeeService employeeService;
    private final UserService userService;
    private final RedisService redisService;

    /**
     * Adds access for employees to a supervisor.
     * <p>
     * For each request, the supervisor is fetched by expertis, then the employee relationships
     * are inserted into the database. The cache is updated accordingly.
     *
     * @param employees List of requests containing supervisor expertis and employee details.
     * @return List of employee expertis that were saved.
     */
    @Override
    @Transactional
    public List<String> addEmployeeAccessForSupervisor(List<RequestSetEmployeeToSupervisor> employees) {
        CustomUserDetails user = new SecurityUtils().getCurrentUser();

        List<String> unCorrectSaveExpertis = new ArrayList<>();

        log.info("Starting addAccess for {} request(s).", employees.size());

        employees.forEach(request -> {
            String supervisorExpertis = request.getSupervisorExpertis();
            EmployeeDTO supervisor = employeeService.getUsersByExpertis(supervisorExpertis);

            if (supervisor == null) {
                log.warn("Supervisor not found for expertis: {}", request.getSupervisorExpertis());
                return;
            }

            if (Boolean.TRUE.equals(supervisor.getIsSupervisor())) {
                List<String> listExpertis = new ArrayList<>();
                List<LocalDate> listDateTo = new ArrayList<>();
                List<LocalDate> listDateFrom = new ArrayList<>();

                request.getRequests().forEach((key, value) -> {
                    listExpertis.add(key);
                    listDateTo.add(value.getValidTo());
                    listDateFrom.add(value.getValidFrom());
                });

                List<String> currentSaveExpertis = repository.insertEmployeeSupervisors(
                        listExpertis.toArray(new String[0]),
                        listDateFrom.toArray(new LocalDate[0]),
                        listDateTo.toArray(new LocalDate[0]),
                        supervisor.getId(),
                        user.getUserId()
                );

                log.info("Added {} employee(s) for supervisor {}.", currentSaveExpertis.size(), supervisor.getExpertis());

                List<String> employeeExpertis = redisService.getValueFromMapping("supervisorExpertisEmployee", request.getSupervisorExpertis(), new TypeReference<List<String>>() {
                }).orElse(new ArrayList<>());

                if (!employeeExpertis.isEmpty()) {
                    employeeExpertis.addAll(currentSaveExpertis);
                    redisService.saveMapping("supervisorExpertisEmployee", request.getSupervisorExpertis(), employeeExpertis);
                }
                log.debug("Updated Redis cache for supervisor {} with {} employee(s).", request.getSupervisorExpertis(), employeeExpertis.size());

                Map<String, EmployeeFullInformationDTO> allEmployee = employeeService.getEmployeeFullDTO(currentSaveExpertis);
                allEmployee.values().forEach(dto -> {
                    dto.setSupervisorExpertis(supervisorExpertis);
                    dto.setSupervisorName(String.format("%s %s", supervisor.getFirstName(), supervisor.getLastName()));
                });

                redisService.saveAllMapping("userFullMapping", allEmployee);

                unCorrectSaveExpertis.addAll(currentSaveExpertis);
            } else {
                log.warn("User {} is not a supervisor. Skipping addAccess.", request.getSupervisorExpertis());
            }
        });

        log.info("Completed addAccess. Total added: {}.", unCorrectSaveExpertis.size());
        return unCorrectSaveExpertis;
    }

    /**
     * Deletes employee access for a given supervisor.
     * <p>
     * For each request, the method removes the relationship from the database and updates the cache.
     *
     */
    @Override
    @Transactional
    public void deleteEmployeeAccessForSupervisor(RemoveSupervisionRequest request) {

        String supervisorExpertis = request.supervisorExpertis();

        List<String> currentDeletedExpertis = repository.deleteEmployeesForSupervisor(
                request.employeeExpertis().toArray(new String[0]),
                supervisorExpertis
        );

        log.info("Deleted {} employee access(es) for supervisor {}.", currentDeletedExpertis.size(), supervisorExpertis);

        List<String> employeeExpertis = redisService.getValueFromMapping("supervisorExpertisEmployee", supervisorExpertis, new TypeReference<List<String>>() {
        }).orElse(new ArrayList<>());

        if (!employeeExpertis.isEmpty() && !currentDeletedExpertis.isEmpty()) {
            employeeExpertis = employeeExpertis.stream().filter(obj -> !currentDeletedExpertis.contains(obj)).toList();
            redisService.saveMapping("supervisorExpertisEmployee", supervisorExpertis, employeeExpertis);
            log.debug("Updated Redis cache for supervisor {} after deletion.", supervisorExpertis);

            Map<String, EmployeeFullInformationDTO> allEmployee = employeeService.getEmployeeFullDTO(currentDeletedExpertis);

            allEmployee.values().forEach(dto -> {
                dto.setSupervisorExpertis("No Supervisor");
                dto.setSupervisorName("No Supervisor");
            });

            redisService.saveAllMapping("userFullMapping", allEmployee);
        }
        log.info("Completed deleteAccess.");
    }

    /**
     * Retrieves a list of employees who do not have a supervisor for the current user's site.
     * <p>
     * The method obtains the current user from the security context, then fetches the supervisor-related
     * information (SupervisorAllInformationDTO) using the current user's ID. It then uses the site name
     * from that information to query the repository for employees without supervisors.
     *
     * @return a list of {@code PreviewEmployeeDTO} objects representing employees without a supervisor.
     * @throws AccessDeniedException if the current user is not authenticated.
     */
    @Override
    public List<PreviewEmployeeDTO> getEmployeeWithoutSupervisors() {
        log.info("Fetching employees without supervisors for the current user.");

        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        log.debug("Current user ID: {}", currentUser.getUserId());

        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(null, currentUser.getUserId());
        log.debug("Retrieved supervisor information for user: {} with siteName: {}", currentUser.getUserId(), allInformation.getSiteName());

        List<PreviewEmployeeDTO> result = repository.getEmployeeWithoutSupervisor(allInformation.getSiteName());
        log.info("Found {} employees without supervisors for site: {}", result.size(), allInformation.getSiteName());
        return result;
    }

    /**
     * Retrieves a list of supervisors for the current user's site.
     * <p>
     * The method obtains the current user from the security context, then fetches the supervisor-related
     * information (SupervisorAllInformationDTO) using the current user's ID. It then uses the site name
     * from that information to query the repository for supervisors.
     *
     * @return a list of {@code PreviewEmployeeDTO} objects representing supervisors.
     * @throws AccessDeniedException if the current user is not authenticated.
     */
    @Override
    public List<PreviewEmployeeDTO> getSupervisors() {
        log.info("Fetching supervisors for the current user.");
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        log.debug("Current user ID: {}", currentUser.getUserId());

        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(null, currentUser.getUserId());
        log.debug("Retrieved supervisor information for user: {} with siteName: {}", currentUser.getUserId(), allInformation.getSiteName());

        List<PreviewEmployeeDTO> result = repository.getSupervisors(allInformation.getSiteName());
        log.info("Found {} supervisors for site: {}", result.size(), allInformation.getSiteName());
        return result;
    }

    /**
     * Retrieves full employee information for a given supervisor.
     * <p>
     * Attempts to get the employee expertis list from Redis cache first. If cache is empty,
     * fetches from the database, caches the result, and returns the full information.
     *
     * @param expertis The supervisor's expertis.
     * @return List of EmployeeFullInformationDTO for employees under the supervisor.
     */
    @Override
    public List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor(String expertis) {
        log.info("Retrieving all employees for supervisor with expertis: {}", expertis);

        List<String> employeeExpertis = redisService.getValueFromMapping("supervisorExpertisEmployee", expertis, new TypeReference<List<String>>() {
        }).orElse(new ArrayList<>());
        if (employeeExpertis.isEmpty()) {
            log.info("Cache miss for supervisor {}. Querying database.", expertis);

            List<EmployeeFullInformationDTO> fullEmployee = repository.getAllEmployeeBySupervisor(expertis);

            Map<String, EmployeeFullInformationDTO> fullMap = fullEmployee.stream().collect(Collectors.toMap(EmployeeFullInformationDTO::getExpertis, Function.identity()));

            redisService.saveAllMapping("userFullMapping", fullMap);

            redisService.saveMapping("supervisorExpertisEmployee", expertis, fullMap.keySet());

            log.debug("Updated Redis cache for supervisor {} with {} employee(s).", expertis, fullMap.keySet().size());

            return fullEmployee;
        } else {
            log.info("Cache hit for supervisor {}. Retrieving full details from cache.", expertis);

            return employeeService.getEmployeeFullDTO(employeeExpertis).values().stream().toList();
        }
    }

    @Override
    @MaskField
    public List<EmployeeFullInformationDTO> getAllEmployeeBySupervisor() {
        log.info("Fetching supervisors for the current user.");
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        log.debug("Current user ID: {}", currentUser.getUserId());

        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(null, currentUser.getUserId());
        log.debug("Retrieved supervisor information for user: {} with siteName: {}", currentUser.getUserId(), allInformation.getSiteName());

        String expertis = allInformation.getExpertis();

        log.info("Retrieving all employees for supervisor with expertis: {}", expertis);

        List<String> employeeExpertis = redisService.getValueFromMapping("supervisorExpertisEmployee", expertis, new TypeReference<List<String>>() {
        }).orElse(new ArrayList<>());
        if (employeeExpertis.isEmpty()) {
            log.info("Cache miss for supervisor {}. Querying database.", expertis);

            List<EmployeeFullInformationDTO> fullEmployee = repository.getAllEmployeeBySupervisor(expertis);
            Map<String, EmployeeFullInformationDTO> fullMap = fullEmployee.stream().collect(Collectors.toMap(EmployeeFullInformationDTO::getExpertis, Function.identity()));

            redisService.saveAllMapping("userFullMapping", fullMap);

            redisService.saveMapping("supervisorExpertisEmployee", expertis, fullMap.keySet());

            log.debug("Updated Redis cache for supervisor {} with {} employee(s).", expertis, fullMap.keySet().size());

            return fullEmployee;
        } else {
            log.info("Cache hit for supervisor {}. Retrieving full details from cache.", expertis);

            return employeeService.getEmployeeFullDTO(employeeExpertis).values().stream().toList();
        }
    }

    /**
     * Scheduled task to find and delete expired employee access records.
     * <p>
     * This method runs every hour (according to the specified cron expression) and removes expired
     * access records from the database. It then updates the corresponding Redis cache mappings.
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Override
    @Transactional
    public void findExpiredAccessesAndDeleteThem() {
        log.info("Scheduled task started: findExpiredAccessesAndDeleteThem.");

        List<Object[]> rows = repository.findExpiredAccessesAndDeleteThem();

        log.info("Found {} expired access record(s) to delete.", rows.size());

        Map<String, List<String>> supervisorToEmployeeExpertis = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));

        if (!supervisorToEmployeeExpertis.isEmpty()) {
            supervisorToEmployeeExpertis.forEach((key, value) -> {
                List<String> employeeExpertis = redisService.getValueFromMapping("supervisorExpertisEmployee", key, new TypeReference<List<String>>() {
                }).orElse(new ArrayList<>());

                if (!employeeExpertis.isEmpty()) {
                    employeeExpertis = employeeExpertis.stream().filter(eachObj -> !value.contains(eachObj)).toList();
                    redisService.saveMapping("supervisorExpertisEmployee", key, employeeExpertis);
                }
            });
        }

        log.info("Scheduled task completed: findExpiredAccessesAndDeleteThem.");
    }
}
