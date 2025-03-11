package com.example.ws.microservices.firstmicroservices.response.attendance.gd;

import com.example.ws.microservices.firstmicroservices.dto.ShiftTimeWorkDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceStatusDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseScheduleTemplate {
    List<ShiftTimeWorkDTO> shiftTimeWork;
    List<AttendanceStatusDTO> attendanceStatus;
}
