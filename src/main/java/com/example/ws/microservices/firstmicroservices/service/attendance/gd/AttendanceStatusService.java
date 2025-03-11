package com.example.ws.microservices.firstmicroservices.service.attendance.gd;

import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceStatusDTO;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;

import java.util.List;

public interface AttendanceStatusService {
    List<AttendanceStatusDTO> getAttendanceStatuses();
}
