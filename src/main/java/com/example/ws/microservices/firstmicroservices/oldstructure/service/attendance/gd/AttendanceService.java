package com.example.ws.microservices.firstmicroservices.oldstructure.service.attendance.gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd.AttendanceUpdateDto;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.attendance.gd.AttendanceChangeRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.attendance.gd.EmployeeAttendanceDTO;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    
    void copyScheduleTemplateToEmployee(String scheduleTemplateName, List<String> employeeExpertis);
    List<EmployeeAttendanceDTO> getAttendanceDTOByEmployeeIdAndDate(List<String> employeeExpertis, LocalDate startDate, LocalDate endDate);
    List<Attendance> getAttendanceByEmployeeIdAndDate(List<Long> employeeIds, LocalDate startDate, LocalDate endDate);
    List<Attendance> getAllAttendanceByDate(Instant startDate, Instant endDate);
    void updateAttendance(Attendance attendance);
    void updateAttendanceDay(AttendanceChangeRequest updatedDto, Principal principal);
    void updateAttendanceBulk(List<AttendanceChangeRequest> updates, Principal principal);
    List<AttendanceUpdateDto> updateAndFetch(List<Integer> ids, List<String> comments, LocalDate date, String statusCode);

}
