package com.example.ws.microservices.firstmicroservices.service.attendance.gd;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.ScheduleTemplateDTO;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.ScheduleTemplateRequest;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleTemplateService {

    List<PreviewEmployeeDTO> getAllEmployeeWithoutScheduleTemplateBeetwenDate(LocalDate startDate, LocalDate endDate);
    ResponseScheduleTemplate getScheduleTemplate();
    List<ScheduleTemplateDTO> getAllScheduleTemplatePerDate(LocalDate date);
    ScheduleTemplateDTO getScheduleTemplateDTOByName(String name);
    ScheduleTemplate getScheduleTemplateByName(String name);
    List<ScheduleTemplateDTO> getScheduleTemplateByUserId(String userId);
    void saveScheduleTemplate(ScheduleTemplateRequest scheduleTemplate);
    void deleteScheduleTemplateByIdOrName(String idOrName);
    void updateScheduleTemplate(ScheduleTemplateRequest scheduleTemplate);
}
