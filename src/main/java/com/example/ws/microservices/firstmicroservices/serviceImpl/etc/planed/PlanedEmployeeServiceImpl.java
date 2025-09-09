package com.example.ws.microservices.firstmicroservices.serviceImpl.etc.planed;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceUpdateDto;
import com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.PositionDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.Employee;
import com.example.ws.microservices.firstmicroservices.entity.etc.planed.PlanedEmployee;
import com.example.ws.microservices.firstmicroservices.entity.etc.planed.PlanedTraining;
import com.example.ws.microservices.firstmicroservices.repository.etc.planed.PlanedEmployeeRepository;
import com.example.ws.microservices.firstmicroservices.repository.etc.planed.PlanedRepositoryCustom;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.AttendanceChangeRequest;
import com.example.ws.microservices.firstmicroservices.request.etc.MultiPlanedEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.secure.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.AttendanceService;
import com.example.ws.microservices.firstmicroservices.service.etc.planed.PlanedEmployeeService;
import com.example.ws.microservices.firstmicroservices.service.vision.PositionService;
import com.example.ws.microservices.firstmicroservices.utils.CompressionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanedEmployeeServiceImpl implements PlanedEmployeeService {

    private final PlanedEmployeeRepository planedEmployeeRepository;
    private final AttendanceService attendanceService;
    private final PlanedRepositoryCustom planedRepositoryCustom;
    private final PositionService positionService;
    private final EmployeeService employeeService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PlanedEmployeeDTO> getAllEmployeesByTrainingIds(List<Long> trainingIds) {
        return planedEmployeeRepository.getAllEmployeeDTOByIdTraining(trainingIds);
    }

    @Override
    @Transactional
    public void savePlanedEmployeeTraining(MultiPlanedEmployeeRequest request) {
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();

        int trainingIds = request.planingId();
        List<PlanedEmployee> entity = request.employeeIds().stream().map(obj -> {
            PlanedEmployee planedEmployee = new PlanedEmployee();
            planedEmployee.setEmployee(entityManager.getReference(Employee.class, obj));
            planedEmployee.setIsPresent(Boolean.FALSE);
            planedEmployee.setPlanedTraining(entityManager.getReference(PlanedTraining.class, trainingIds));
            planedEmployee.setUserId(currentUser.getUserId());
            planedEmployee.setDate(Instant.now());
            return planedEmployee;
        }).toList();

        planedRepositoryCustom.savePlanedEmployeeTraining(entity);

        List<Integer> idsEmployee = request.employeeIds();
        List<String> comments = Collections.nCopies(idsEmployee.size(), "Planned SZK");

        List<AttendanceUpdateDto> attendees = attendanceService.updateAndFetch(idsEmployee, comments, request.dateTraining(), "Training");

        prepareAttendanceSend(request, attendees);
    }

    @Override
    @Transactional
    public void deletePlanedEmployeeTraining(MultiPlanedEmployeeRequest request) {
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();

        int trainingIds = request.planingId();
        List<PlanedEmployee> entity = request.employeeIds().stream().map(obj -> {
            PlanedEmployee planedEmployee = new PlanedEmployee();
            planedEmployee.setEmployee(entityManager.getReference(Employee.class, obj));
            planedEmployee.setPlanedTraining(entityManager.getReference(PlanedTraining.class, trainingIds));
            return planedEmployee;
        }).toList();

        planedRepositoryCustom.deletePlanedEmployeeTraining(entity, currentUser.getUserId());

        List<Integer> idsEmployee = request.employeeIds();
        List<String> comments = Collections.nCopies(idsEmployee.size(), "");

        List<AttendanceUpdateDto> attendees = attendanceService.updateAndFetch(idsEmployee, comments, request.dateTraining(), "Planed Work Day");

        prepareAttendanceSend(request, attendees);
    }

    private void prepareAttendanceSend(MultiPlanedEmployeeRequest request, List<AttendanceUpdateDto> attendees) {
        resolveTopicsForDate(request.dateTraining()).forEach(topic -> {

            List<AttendanceChangeRequest> updatedResponse = attendees.stream().map(obj -> {
                return AttendanceChangeRequest.builder()
                        .attendanceId(obj.getAttendanceId())
                        .employeeId(obj.getEmployeeId())
                        .attendanceDate(obj.getAttendanceDate())
                        .shiftCode(obj.getShiftCode())
                        .statusCode(obj.getStatusCode())
                        .departmentName(obj.getDepartmentName())
                        .houseWorked(obj.getHouseWorked())
                        .comment(obj.getComment())
                        .topicDate(topic)
                        .dayIndex(request.dateTraining().getDayOfMonth() - 1)
                        .build();
            }).toList();

            String json = null;

            try {
                json = objectMapper.writeValueAsString(updatedResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String compressed = CompressionUtils.compress(json);
            messagingTemplate.convertAndSend(topic, compressed);
        });
    }

    @Override
    public void setPlanedEmployeeTraining(MultiPlanedEmployeeRequest request) {

    }

    @Override
    public List<PreviewEmployeeDTO> getAllEmployeesAvailableForTraining(LocalDate dateTraining, LocalTime startTime, LocalTime endTime, Set<String> positionName, String nameDoc) {
        List<PositionDTO> allPositions = positionService.getPositionsBySupervisorSite();
        List<Integer> idsPositions = allPositions.stream().filter(pos -> positionName.contains(pos.getName())).map(PositionDTO::getId).toList();
        return employeeService.getAllEmployeesWithPlannedShiftMatching(dateTraining, startTime, endTime, idsPositions, nameDoc);
    }

    private List<String> resolveTopicsForDate(LocalDate date) {
        YearMonth ym = YearMonth.from(date);
        YearMonth prev = ym.minusMonths(1);
        YearMonth next = ym.plusMonths(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String prevStart = prev.atDay(1).format(formatter);
        String currentStart = ym.atDay(1).format(formatter);
        String currentEnd = ym.atEndOfMonth().format(formatter);
        String nextEnd = next.atEndOfMonth().format(formatter);

        return List.of(
                "/topic/attendance/" + prevStart + "_" + currentEnd,
                "/topic/attendance/" + currentStart + "_" + nextEnd
        );
    }
}
