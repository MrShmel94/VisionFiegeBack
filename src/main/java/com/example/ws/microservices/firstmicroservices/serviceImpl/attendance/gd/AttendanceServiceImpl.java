package com.example.ws.microservices.firstmicroservices.serviceImpl.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.repository.attendance.gd.AttendanceRepository;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.AttendanceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Override
    public void copyScheduleTemplateToEmployee(ScheduleTemplate scheduleTemplate, List<Long> employeeIds) {

    }

    @Override
    public Map<String, List<Attendance>> getAttendanceByEmployeeIdAndDate(List<Long> employeeIds, Instant startDate, Instant endDate) {
        return Map.of();
    }

    @Override
    public List<Attendance> getAllAttendanceByDate(Instant startDate, Instant endDate) {
        return List.of();
    }

    @Override
    public void updateAttendance(Attendance attendance) {

    }
}
