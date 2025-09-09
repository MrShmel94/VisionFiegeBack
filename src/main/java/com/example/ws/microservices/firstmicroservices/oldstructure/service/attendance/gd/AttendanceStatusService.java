package com.example.ws.microservices.firstmicroservices.oldstructure.service.attendance.gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd.AttendanceStatusDTO;

import java.util.List;

public interface AttendanceStatusService {
    List<AttendanceStatusDTO> getAttendanceStatuses();
}
