package com.example.ws.microservices.firstmicroservices.request.attendance.gd;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.Map;

@Getter
@Setter
@ValidScheduleTemplate
public class ScheduleTemplateRequest {

    @NotNull(message = "Period is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-yyyy")
    private YearMonth period;

    @NotNull(message = "Schedule must not be null")
    private Map<String, DayScheduleRequest> schedule;

    @NotNull(message = "Name of schedule template is required")
    @Size(max = 512)
    private String nameScheduleTemplate;
    private String description;

}
