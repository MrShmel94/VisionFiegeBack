package com.example.ws.microservices.firstmicroservices.controller;


import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.ScheduleTemplateDTO;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.ScheduleTemplateRequest;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.ScheduleTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final ScheduleTemplateService scheduleTemplateService;

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
        return scheduleTemplateService.getScheduleTemplateByName(name);
    }
}
