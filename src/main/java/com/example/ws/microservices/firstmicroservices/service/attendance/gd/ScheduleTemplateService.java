package com.example.ws.microservices.firstmicroservices.service.attendance.gd;

import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.ScheduleTemplateDTO;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.ScheduleTemplateRequest;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;

import java.util.List;
import java.util.Optional;

public interface ScheduleTemplateService {

    ResponseScheduleTemplate getScheduleTemplate();
    List<ScheduleTemplateDTO> getAllScheduleTemplate();
    ScheduleTemplateDTO getScheduleTemplateByName(String name);
    List<ScheduleTemplateDTO> getScheduleTemplateByUserId(String userId);
    void saveScheduleTemplate(ScheduleTemplateRequest scheduleTemplate);
    void deleteScheduleTemplateByIdOrName(String idOrName);
    void updateScheduleTemplate(ScheduleTemplateRequest scheduleTemplate);
}
