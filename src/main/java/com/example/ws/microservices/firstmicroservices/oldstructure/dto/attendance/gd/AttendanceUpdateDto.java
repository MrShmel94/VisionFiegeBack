package com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd;

import java.time.Instant;
import java.time.LocalDate;

public interface AttendanceUpdateDto {
    Long getAttendanceId();
    Long getEmployeeId();
    LocalDate getAttendanceDate();
    String getShiftCode();
    String getStatusCode();
    String getDepartmentName();
    String getSiteName();
    Double getHouseWorked();
    String getComment();
    Instant getCreatedAt();
}
