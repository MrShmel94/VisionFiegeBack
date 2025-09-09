package com.example.ws.microservices.firstmicroservices.domain.employeedata.shifttimework;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTimeWorkDTO {

    private Integer shiftId;
    private String shiftName;
    private String shiftCode;
    private LocalTime startTime;
    private LocalTime endTime;

}
