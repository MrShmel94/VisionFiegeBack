package com.example.ws.microservices.firstmicroservices.service.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    
    void copyScheduleTemplateToEmployee(ScheduleTemplate scheduleTemplate, List<Long> employeeIds);
    Map<String, List<Attendance>> getAttendanceByEmployeeIdAndDate(List<Long> employeeIds, Instant startDate, Instant endDate);
    List<Attendance> getAllAttendanceByDate(Instant startDate, Instant endDate);
    void updateAttendance(Attendance attendance);

}
