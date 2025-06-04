package com.example.ws.microservices.firstmicroservices.serviceImpl.attendance.gd;

import com.example.ws.microservices.firstmicroservices.customError.VerificationException;
import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.ShiftTimeWorkDTO;
import com.example.ws.microservices.firstmicroservices.dto.SmallInformationSupervisorDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceStatusDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.ScheduleTemplateDTO;
import com.example.ws.microservices.firstmicroservices.entity.attendance.DaySchedule;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.repository.attendance.gd.ScheduleTemplateRepository;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.DayScheduleRequest;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.ScheduleTemplateRequest;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.service.ShiftTimeWorkService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.AttendanceStatusService;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.ScheduleTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService {

    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ShiftTimeWorkService shiftTimeWorkService;
    private final AttendanceStatusService attendanceStatusService;
    private final UserService userService;


    @Override
    public List<PreviewEmployeeDTO> getAllEmployeeWithoutScheduleTemplateBeetwenDate(LocalDate startDate, LocalDate endDate) {
        return scheduleTemplateRepository.getAllEmployeeWithoutScheduleTemplateBeetwenDate(startDate, endDate);
    }

    @Override
    public ResponseScheduleTemplate getScheduleTemplate() {
        List<AttendanceStatusDTO> attendanceStatus = attendanceStatusService.getAttendanceStatuses();
        List<ShiftTimeWorkDTO> shiftTimeWork = shiftTimeWorkService.getShiftTimeWorkByNameSite("GD");

        return ResponseScheduleTemplate.builder()
                .attendanceStatus(attendanceStatus)
                .shiftTimeWork(shiftTimeWork)
                .build();
    }

    @Override
    public List<ScheduleTemplateDTO> getAllScheduleTemplatePerDate(LocalDate date) {
        List<ScheduleTemplate> getAllTemplates = scheduleTemplateRepository.findAllByDate(date.withDayOfMonth(1));
        ResponseScheduleTemplate template = getScheduleTemplate();
        return getAllTemplates.stream().map(obj -> convertEntityToScheduleTemplateDTO(obj, template)).collect(Collectors.toList());
    }

    @Override
    public ScheduleTemplate getScheduleTemplateByName(String name) {
        Optional<ScheduleTemplate> optionalScheduleTemplate = scheduleTemplateRepository
                .findByNameScheduleTemplate(name);

        if(optionalScheduleTemplate.isEmpty()) {
            throw new VerificationException("Schedule Template with name " + name + " not found");
        }

        return optionalScheduleTemplate.get();
    }

    @Override
    public ScheduleTemplateDTO getScheduleTemplateDTOByName(String name) {
        Optional<ScheduleTemplate> optionalScheduleTemplate = scheduleTemplateRepository
                .findByNameScheduleTemplate(name);

        if(optionalScheduleTemplate.isEmpty()) {
            throw new VerificationException("Schedule Template with name " + name + " not found");
        }

        ScheduleTemplate scheduleTemplate = optionalScheduleTemplate.get();
        ResponseScheduleTemplate template = getScheduleTemplate();

        return convertEntityToScheduleTemplateDTO(scheduleTemplate, template);
    }

    private ScheduleTemplateDTO convertEntityToScheduleTemplateDTO(ScheduleTemplate scheduleTemplate, ResponseScheduleTemplate template) {
        SmallInformationSupervisorDTO informationAboutUser = userService
                .getSmallInformationSupervisor(scheduleTemplate.getUserId()).orElse(SmallInformationSupervisorDTO.builder()
                        .firstName("Unknown")
                        .lastName("Unknown")
                        .build());

        Map<Integer, ShiftTimeWorkDTO> shiftTimeWorkMap = template.getShiftTimeWork().stream()
                .collect(Collectors.toMap(ShiftTimeWorkDTO::getShiftId, Function.identity()));
        Map<Integer, AttendanceStatusDTO> attendanceStatusMap = template.getAttendanceStatus().stream()
                .collect(Collectors.toMap(AttendanceStatusDTO::getId, Function.identity()));

        Map<String, DayScheduleRequest> mapDaySchedule = scheduleTemplate.getSchedule().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new DayScheduleRequest(attendanceStatusMap.get(entry.getValue().getStatusId()).getStatusCode()
                        ,shiftTimeWorkMap.get(entry.getValue().getShiftId()).getShiftCode())
                ));


        return ScheduleTemplateDTO.builder()
                .id(scheduleTemplate.getId())
                .period(scheduleTemplate.getDate())
                .nameScheduleTemplate(scheduleTemplate.getNameScheduleTemplate())
                .description(scheduleTemplate.getDescription())
                .fullNameCreator(informationAboutUser)
                .schedule(mapDaySchedule)
                .build();
    }

    @Override
    public List<ScheduleTemplateDTO> getScheduleTemplateByUserId(String userId) {
        return List.of();
    }

    @Override
    public void saveScheduleTemplate(ScheduleTemplateRequest scheduleTemplate) {
        ScheduleTemplate scheduleTemplateEntity = new ScheduleTemplate();
        scheduleTemplateEntity.setDate(scheduleTemplate.getPeriod().atDay(1));
        scheduleTemplateEntity.setCreatedAt(Instant.now());
        scheduleTemplateEntity.setDescription(scheduleTemplate.getDescription());
        scheduleTemplateEntity.setNameScheduleTemplate(scheduleTemplate.getNameScheduleTemplate());

        scheduleTemplateEntity.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());

        ResponseScheduleTemplate template = getScheduleTemplate();
        Map<String, ShiftTimeWorkDTO> shiftTimeWorkMap = template.getShiftTimeWork().stream()
                .collect(Collectors.toMap(ShiftTimeWorkDTO::getShiftCode, Function.identity()));
        Map<String, AttendanceStatusDTO> attendanceStatusMap = template.getAttendanceStatus().stream()
                .collect(Collectors.toMap(AttendanceStatusDTO::getStatusCode, Function.identity()));

        Map<String, DaySchedule> mapDaySchedule = scheduleTemplate.getSchedule().entrySet().stream()
                .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new DaySchedule(shiftTimeWorkMap.get(entry.getValue().getShiftCode()).getShiftId()
                ,attendanceStatusMap.get(entry.getValue().getStatusCode()).getId())
        ));

        scheduleTemplateEntity.setSchedule(mapDaySchedule);
        scheduleTemplateRepository.save(scheduleTemplateEntity);
    }

    @Override
    public void deleteScheduleTemplateByIdOrName(String idOrName) {

    }

    @Override
    public void updateScheduleTemplate(ScheduleTemplateRequest scheduleTemplate) {

    }
}
