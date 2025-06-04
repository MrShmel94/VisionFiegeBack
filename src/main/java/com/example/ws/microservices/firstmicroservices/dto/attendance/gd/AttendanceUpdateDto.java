package com.example.ws.microservices.firstmicroservices.dto.attendance.gd;

import java.time.Instant;
import java.time.LocalDate;

public interface AttendanceUpdateDto {
    Long getAttendanceId();
    Long getEmployeeId();
    LocalDate getAttendanceDate();
    String getShiftCode();
    String getStatusCode();
    String getDepartmentName();
    Double getHouseWorked();
    String getComment();
    Instant getCreatedAt();
}
