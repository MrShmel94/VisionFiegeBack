package com.example.ws.microservices.firstmicroservices.oldstructure.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.attendance.gd.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepositoryCustom {
    void bulkUpsert(List<Attendance> attendances);
    void bulkUpdate(List<Attendance> attendances);
    void createPartitionIfNotExists(LocalDate targetDate);
}
