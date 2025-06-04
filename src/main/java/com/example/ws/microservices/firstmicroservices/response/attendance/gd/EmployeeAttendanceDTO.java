package com.example.ws.microservices.firstmicroservices.response.attendance.gd;

import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceEmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeAttendanceDTO {
    private AttendanceEmployeeDTO employee;
    private List<AttendanceDTO> attendance;
}