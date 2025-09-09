package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.attendance.gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd.AttendanceStatusDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.attendance.gd.AttendanceStatusRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.attendance.gd.AttendanceStatusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class AttendanceStatusServiceImpl implements AttendanceStatusService {

    private AttendanceStatusRepository attendanceStatusRepository;

    @Override
    public List<AttendanceStatusDTO>  getAttendanceStatuses() {
        return attendanceStatusRepository.findAll().stream().map(obj -> {
            return AttendanceStatusDTO.builder()
                    .id(obj.getId())
                    .statusCode(obj.getStatusCode())
                    .statusName(obj.getStatusName())
                    .description(obj.getDescription())
                    .color(obj.getColor())
                    .build();
        }).toList();
    }
}
