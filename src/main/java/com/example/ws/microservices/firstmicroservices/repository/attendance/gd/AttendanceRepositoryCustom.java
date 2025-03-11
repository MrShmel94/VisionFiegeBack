package com.example.ws.microservices.firstmicroservices.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;

import java.util.List;

public interface AttendanceRepositoryCustom {
    void bulkUpsert(List<Attendance> attendances);
}
