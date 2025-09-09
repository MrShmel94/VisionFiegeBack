package com.example.ws.microservices.firstmicroservices.oldstructure.request.attendance.gd;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class GetAttendanceList {

    private List<String> expertis;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

}
