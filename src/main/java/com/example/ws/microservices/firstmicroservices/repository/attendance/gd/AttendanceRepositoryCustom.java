package com.example.ws.microservices.firstmicroservices.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.request.attendance.gd.AttendanceChangeRequest;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepositoryCustom {
    void bulkUpsert(List<Attendance> attendances);
    void bulkUpdate(List<Attendance> attendances);
    void createPartitionIfNotExists(LocalDate targetDate);
}
