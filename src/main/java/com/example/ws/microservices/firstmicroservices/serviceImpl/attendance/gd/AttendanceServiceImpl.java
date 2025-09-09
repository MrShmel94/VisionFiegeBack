package com.example.ws.microservices.firstmicroservices.serviceImpl.attendance.gd;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeFullInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceUpdateDto;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.SiteDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.Employee;
import com.example.ws.microservices.firstmicroservices.domain.attendace.shifttimework.ShiftTimeWork;
import com.example.ws.microservices.firstmicroservices.entity.attendance.DaySchedule;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.AttendanceStatus;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Department;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Site;
import com.example.ws.microservices.firstmicroservices.repository.attendance.gd.AttendanceRepository;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.AttendanceChangeRequest;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.EmployeeAttendanceDTO;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.secure.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.SiteService;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.AttendanceService;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.ScheduleTemplateService;
import com.example.ws.microservices.firstmicroservices.service.vision.DepartmentService;
import com.example.ws.microservices.firstmicroservices.utils.CompressionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;
    private final ScheduleTemplateService scheduleTemplateService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final DepartmentService departmentService;
    private final SiteService siteService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void copyScheduleTemplateToEmployee(String scheduleTemplateName, List<String> employeeExpertis) {
        ScheduleTemplate scheduleTemplate = scheduleTemplateService.getScheduleTemplateByName(scheduleTemplateName);
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        EmployeeFullInformationDTO fullInformationCurrentUser = employeeService.getEmployeeFullInformation(currentUser.getExpertis());

        List<EmployeeFullInformationDTO> idEmployee = employeeService.getEmployeeFullDTO(employeeExpertis).values().stream().toList();
        List<Attendance> attendances = new ArrayList<>();
        Map<String, Integer> departments = departmentService.getDepartmentsBySupervisorSite().stream().collect(Collectors.toMap(
                DepartmentDTO::getName, DepartmentDTO::getId
        ));

        Map<String, Integer> sites = siteService.getSites().stream().collect(Collectors.toMap(
                SiteDTO::getName, SiteDTO::getId
        ));

        ResponseScheduleTemplate template = scheduleTemplateService.getScheduleTemplate();
        Integer statusX = template.getAttendanceStatus().stream().filter(el -> el.getStatusCode().equalsIgnoreCase("x")).findFirst().orElseThrow().getId();
        Integer shiftDayOff = template.getShiftTimeWork().stream().filter(el -> el.getShiftCode().equalsIgnoreCase("w")).findFirst().orElseThrow().getShiftId();

        for (Map.Entry<String, DaySchedule> entry : scheduleTemplate.getSchedule().entrySet()) {
            LocalDate createDate = scheduleTemplate.getDate().withDayOfMonth(Integer.parseInt(entry.getKey()));

            idEmployee.forEach(eachEmployee -> {
                if (fullInformationCurrentUser.getSiteName().equals(eachEmployee.getSiteName()) || fullInformationCurrentUser.getSiteName().equals(eachEmployee.getTemporaryAssignmentSiteName())) {
                    boolean bool = checkDateContractInRange(createDate, eachEmployee.getDateStartContract(), eachEmployee.getDateFinishContract());

                    attendances.add(Attendance.builder()
                            .employee(entityManager.getReference(Employee.class, eachEmployee.getId()))
                            .date(createDate)
                            .shift(entityManager.getReference(ShiftTimeWork.class, bool ? entry.getValue().getShiftId() : shiftDayOff))
                            .status(entityManager.getReference(AttendanceStatus.class, bool ? entry.getValue().getStatusId() : statusX))
                            .department(entityManager.getReference(Department.class, departments.get(eachEmployee.getDepartmentName())))
                            .site(entityManager.getReference(Site.class, sites.get(fullInformationCurrentUser.getSiteName())))
                            .userId(currentUser.getUserId())
                            .comment("")
                            .build());
                }
            });
        }

        try {

            attendances.stream()
                    .map(a -> a.getDate().withDayOfMonth(1))
                    .distinct()
                    .forEach(attendanceRepository::createPartitionIfNotExists);

            attendanceRepository.bulkUpsert(attendances);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private boolean checkDateContractInRange(LocalDate dateSchedule, LocalDate startContract, LocalDate endContract) {
        return !dateSchedule.isBefore(startContract) && !dateSchedule.isAfter(endContract);
    }

    @Override
    public List<EmployeeAttendanceDTO> getAttendanceDTOByEmployeeIdAndDate(@Nullable List<String> employeeExpertis, LocalDate startDate, LocalDate endDate) {

        List<String> allExpertis = new ArrayList<>();

        if (employeeExpertis == null) {
            allExpertis = employeeService.getAllExpertis();

            if (allExpertis == null) {
                return Collections.emptyList();
            }
        }

        List<AttendanceEmployeeDTO> allEmployees = employeeService.getEmployeeFullDTO(allExpertis).values().stream().map(obj -> {
            return AttendanceEmployeeDTO.builder()
                    .id(obj.getId())
                    .shiftName(obj.getShiftName())
                    .teamName(obj.getTeamName())
                    .positionName(obj.getPositionName())
                    .siteName(obj.getSiteName())
                    .firstName(obj.getFirstName())
                    .lastName(obj.getLastName())
                    .departmentName(obj.getDepartmentName())
                    .expertis(obj.getExpertis())
                    .supervisor(obj.getSupervisorName())
                    .build();
        }).toList();
        List<Long> idEmployee = allEmployees.stream().map(AttendanceEmployeeDTO::getId).toList();
        List<AttendanceDTO> attendanceDTOList = attendanceRepository.findAllByEmployeeIdInAndDateBetween(idEmployee, startDate, endDate);

        Map<Long, List<AttendanceDTO>> attendanceGroupedByEmployeeId = attendanceDTOList.stream()
                .collect(Collectors.groupingBy(AttendanceDTO::getEmployeeId));

        List<EmployeeAttendanceDTO> listEmployeeAttendance = new ArrayList<>();

        for (AttendanceEmployeeDTO employee : allEmployees) {
            List<AttendanceDTO> attendances = attendanceGroupedByEmployeeId.getOrDefault(employee.getId(), Collections.emptyList());

            if (!attendances.isEmpty()) {
                listEmployeeAttendance.add(EmployeeAttendanceDTO.builder()
                        .attendance(attendances)
                        .employee(employee)
                        .build());
            }
        }

        return listEmployeeAttendance;
    }

    @Override
    public List<Attendance> getAttendanceByEmployeeIdAndDate(List<Long> employeeIds, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findAllByEmployee_IdInAndDateBetween(employeeIds, startDate, endDate);
    }

    @Override
    public List<Attendance> getAllAttendanceByDate(Instant startDate, Instant endDate) {
        return List.of();
    }

    @Override
    public void updateAttendance(Attendance attendance) {
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        attendance.setUserId(currentUser.getUserId());
        attendance.setCreatedAt(Instant.now());

        attendanceRepository.save(attendance);
    }

    @SneakyThrows
    @Transactional
    public void updateAttendanceDay(AttendanceChangeRequest updatedDto, Principal principal) {
        CustomUserDetails currentUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        ResponseScheduleTemplate templateId = scheduleTemplateService.getScheduleTemplate();

        Attendance attendance = Attendance.builder()
                .id(updatedDto.getAttendanceId())
                .employee(entityManager.getReference(Employee.class, updatedDto.getEmployeeId()))
                .date(updatedDto.getAttendanceDate())
                .shift(entityManager.getReference(ShiftTimeWork.class, templateId.getShiftTimeWork().stream()
                        .filter(obj -> obj.getShiftName().equals(updatedDto.getShiftCode())).findFirst().orElseThrow().getShiftId()))
                .status(entityManager.getReference(AttendanceStatus.class, templateId.getAttendanceStatus().stream()
                        .filter(obj -> obj.getStatusCode().equals(updatedDto.getStatusCode())).findFirst().orElseThrow().getId()))
                .hoursWorked(updatedDto.getHouseWorked())
                .comment(updatedDto.getComment())
                .userId(currentUser.getUserId())
                .build();


        attendanceRepository.saveAttendance(
                attendance.getEmployee().getId(),
                attendance.getDate(),
                attendance.getShift().getId().longValue(),
                attendance.getSite().getId().longValue(),
                attendance.getDepartment().getId().longValue(),
                attendance.getStatus().getId().longValue(),
                attendance.getHoursWorked(),
                attendance.getComment(),
                attendance.getUserId()
        );


        String json = objectMapper.writeValueAsString(updatedDto);
        String compressed = CompressionUtils.compress(json);

        String topic = "/topic/attendanceList/" + updatedDto.getTopicDate();
        System.out.printf("Send to topic -> %s", topic);
        messagingTemplate.convertAndSend(topic, compressed);
    }

    @Transactional
    @SneakyThrows
    public void updateAttendanceBulk(List<AttendanceChangeRequest> updates, Principal principal) {
        CustomUserDetails currentUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        Map<String, Integer> mapDepartment = departmentService.getDepartmentsBySupervisorSite(currentUser.getUserId())
                .stream().collect(Collectors.toMap(DepartmentDTO::getName, DepartmentDTO::getId));

        ResponseScheduleTemplate templateId = scheduleTemplateService.getScheduleTemplate();

        List<Attendance> attendanceList = updates.stream()
                .map(updatedDto -> Attendance.builder()
                        .id(updatedDto.getAttendanceId())
                        .employee(entityManager.getReference(Employee.class, updatedDto.getEmployeeId()))
                        .date(updatedDto.getAttendanceDate())
                        .shift(entityManager.getReference(ShiftTimeWork.class, templateId.getShiftTimeWork().stream()
                                .filter(obj -> obj.getShiftName().equals(updatedDto.getShiftCode()))
                                .findFirst().orElseThrow().getShiftId()))
                        .status(entityManager.getReference(AttendanceStatus.class, templateId.getAttendanceStatus().stream()
                                .filter(obj -> obj.getStatusCode().equals(updatedDto.getStatusCode()))
                                .findFirst().orElseThrow().getId()))
                        .department(entityManager.getReference(Department.class, mapDepartment.get(updatedDto.getDepartmentName())))
                        .hoursWorked(updatedDto.getHouseWorked())
                        .comment(updatedDto.getComment())
                        .userId(currentUser.getUserId())
                        .build())
                .toList();

        attendanceRepository.bulkUpdate(attendanceList);

        String json = objectMapper.writeValueAsString(updates);
        String compressed = CompressionUtils.compress(json);

        String topic = "/topic/attendanceList/" + updates.get(0).getTopicDate();
        messagingTemplate.convertAndSend(topic, compressed);
    }

    @Override
    @Transactional
    public List<AttendanceUpdateDto> updateAndFetch(List<Integer> ids, List<String> comments, LocalDate date, String statusCode) {
        return attendanceRepository.bulkUpdateStatusAndComment(
                ids.stream().map(Long::valueOf).toArray(Long[]::new),
                comments.toArray(String[]::new),
                date,
                statusCode
        );
    }
}
