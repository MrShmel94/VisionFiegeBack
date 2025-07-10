package com.example.ws.microservices.firstmicroservices.dto.attendance.gd;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class AttendanceDTO {

    private Long attendanceId;
    private Long employeeId;
    private LocalDate attendanceDate;
    private String shiftCode;
    private String statusCode;
    private String departmentName;
    private String siteName;
    private Double houseWorked;
    private String comment;
    private Instant createdAt;

}
