package com.example.ws.microservices.firstmicroservices.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, AttendanceRepositoryCustom {
    List<Attendance> findAllByEmployeeIdInAndDateBetween(List<Long> employeeIds, Instant startDate, Instant endDate);
    List<Attendance> findAllByDateBetween(Instant startDate, Instant endDate);
}
