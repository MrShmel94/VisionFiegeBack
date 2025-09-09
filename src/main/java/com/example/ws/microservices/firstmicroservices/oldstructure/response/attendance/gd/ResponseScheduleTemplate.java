package com.example.ws.microservices.firstmicroservices.oldstructure.response.attendance.gd;

import com.example.ws.microservices.firstmicroservices.domain.attendace.shifttimework.ShiftTimeWorkDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd.AttendanceStatusDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseScheduleTemplate {
    List<ShiftTimeWorkDTO> shiftTimeWork;
    List<AttendanceStatusDTO> attendanceStatus;
}
