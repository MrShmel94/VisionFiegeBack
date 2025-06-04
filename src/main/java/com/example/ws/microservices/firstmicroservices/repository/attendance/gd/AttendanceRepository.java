package com.example.ws.microservices.firstmicroservices.repository.attendance.gd;

import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceUpdateDto;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.Attendance;
import com.example.ws.microservices.firstmicroservices.entity.attendance.gd.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, AttendanceRepositoryCustom {

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceDTO(
           at.id, emp.id, at.date, shw.name, att.statusCode, dep.name, at.hoursWorked, at.comment, at.createdAt
           ) FROM Attendance at
           JOIN ShiftTimeWork shw ON at.shift.id = shw.id
           JOIN AttendanceStatus att ON att.id = at.status.id
           JOIN Employee emp ON emp.id = at.employee.id
           JOIN Department dep ON dep.id = at.department.id
           WHERE at.employee.id IN :userIds
           AND at.date >= :startDate
           AND at.date <= :endDate
           """)
    List<AttendanceDTO> findAllByEmployeeIdInAndDateBetween(@Param("userIds") List<Long> employeeIds, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    List<Attendance> findAllByDateBetween(Instant startDate, Instant endDate);

    @Query(value = """
                WITH updated AS (
                  UPDATE attendance_gd.attendance a
                  SET comment = v.comment,
                      status_id = s.id
                  FROM (
                    SELECT unnest(:employeeIds) AS employee_id,
                           unnest(:comments) AS comment
                  ) AS v
                  JOIN attendance_gd.attendance_status s ON s.status_name = :statusName
                  WHERE a.employee_id = v.employee_id AND a.date = :date
                  RETURNING a.id, a.employee_id, a.date, a.comment, a.status_id, a.department_id, a.hours_worked, a.created_at, a.shift_id
                )
                SELECT
                  u.id AS attendanceId,
                  u.employee_id AS employeeId,
                  u.date AS attendanceDate,
                  sh.shift_code AS shiftCode,
                  s.status_code AS statusCode,
                  d.name AS departmentName,
                  u.hours_worked AS houseWorked,
                  u.comment,
                  u.created_at AS createdAt
                FROM updated u
                JOIN attendance_gd.attendance_status s ON u.status_id = s.id
                JOIN vision.shift_time_work sh ON sh.id = u.shift_id
                JOIN vision.department d ON d.id = u.department_id
            """, nativeQuery = true)
    List<AttendanceUpdateDto> bulkUpdateStatusAndComment(
            @Param("employeeIds") Long[] employeeIds,
            @Param("comments") String[] comments,
            @Param("date") LocalDate date,
            @Param("statusName") String statusName
    );

    List<Attendance> findAllByEmployee_IdInAndDateBetween(List<Long> employeeIds, LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query(value = """
    INSERT INTO attendance_gd.attendance
    (employee_id, date, shift_id, status_id, hours_worked, comment, user_id)
    VALUES (:employeeId, :date, :shiftId, :statusId, :hoursWorked, :comment, :userId)
    ON CONFLICT (employee_id, date)
    DO UPDATE SET
        shift_id = EXCLUDED.shift_id,
        status_id = EXCLUDED.status_id,
        hours_worked = EXCLUDED.hours_worked,
        comment = EXCLUDED.comment,
        created_at = now();
    """, nativeQuery = true)
    void saveAttendance(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date,
            @Param("shiftId") Long shiftId,
            @Param("statusId") Long statusId,
            @Param("hoursWorked") Double hoursWorked,
            @Param("comment") String comment,
            @Param("userId") String userId
    );
}
