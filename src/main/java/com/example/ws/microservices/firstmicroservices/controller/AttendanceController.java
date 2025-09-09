package com.example.ws.microservices.firstmicroservices.controller;


import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.ScheduleTemplateDTO;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.GetAttendanceList;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.ScheduleTemplateRequest;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.SetScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.AttendanceService;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.ScheduleTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final ScheduleTemplateService scheduleTemplateService;
    private final AttendanceService attendanceService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/attendanceStatus")
    public ResponseScheduleTemplate getAttendanceStatusSite(){
        return scheduleTemplateService.getScheduleTemplate();
    }

    @PostMapping("/scheduleTemplate")
    public ResponseEntity<?> setScheduleTemplate(@Valid @RequestBody ScheduleTemplateRequest request){
        scheduleTemplateService.saveScheduleTemplate(request);
        return ResponseEntity.ok("");
    }

    @GetMapping("/scheduleTemplate/{name}")
    public ScheduleTemplateDTO getScheduleTemplate(@PathVariable("name") String name){
        return scheduleTemplateService.getScheduleTemplateDTOByName(name);
    }

    @PostMapping("/setScheduleTemplate")
    public ResponseEntity<?> setScheduleTemplate(@Valid @RequestBody SetScheduleTemplate setScheduleTemplate){
        attendanceService.copyScheduleTemplateToEmployee(setScheduleTemplate.getScheduleName(), setScheduleTemplate.getExpertisList());
        return ResponseEntity.ok("");
    }

    @PostMapping("/getAttendanceList")
    public ResponseEntity<?> getAttendanceList(@Valid @RequestBody GetAttendanceList getAttendanceList){
        return ResponseEntity.ok(attendanceService.getAttendanceDTOByEmployeeIdAndDate(getAttendanceList.getExpertis(), getAttendanceList.getStartDate(), getAttendanceList.getEndDate()));
    }

    @PostMapping("/setFiles")
    public ResponseEntity<?> setFile(@RequestParam("file") MultipartFile file){

        return ResponseEntity.ok().build();
    }

    @GetMapping("/getListWithout")
    public ResponseEntity<List<PreviewEmployeeDTO>> getListAllEmployeesWithoutAttendance(
            @RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end
    ){
        return ResponseEntity.ok(scheduleTemplateService.getAllEmployeeWithoutScheduleTemplateBeetwenDate(start, end));
    }

    @GetMapping("/getScheduleByDate")
    public ResponseEntity<?> getListAllEmployeesWithoutAttendance(
            @RequestParam("date") LocalDate date
    ){
        return ResponseEntity.ok(scheduleTemplateService.getAllScheduleTemplatePerDate(date));
    }
}
