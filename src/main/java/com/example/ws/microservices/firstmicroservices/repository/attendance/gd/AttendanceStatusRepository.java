package com.example.ws.microservices.firstmicroservices.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Long> {
}
