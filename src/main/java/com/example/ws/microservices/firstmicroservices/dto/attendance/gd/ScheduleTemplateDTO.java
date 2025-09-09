package com.example.ws.microservices.firstmicroservices.dto.attendance.gd;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeesupervisor.SmallInformationSupervisorDTO;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.DayScheduleRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTemplateDTO {

    private Long id;
    private LocalDate period;
    private Map<String, DayScheduleRequest> schedule;
    private String nameScheduleTemplate;
    private String description;
    private SmallInformationSupervisorDTO fullNameCreator;

}
