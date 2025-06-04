package com.example.ws.microservices.firstmicroservices.request.attendance.gd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SetScheduleTemplate {

    @NotNull
    @NotBlank
    private String scheduleName;

    @NotNull
    private List<String> expertisList;
}
