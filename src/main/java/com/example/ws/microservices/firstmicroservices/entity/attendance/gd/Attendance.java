package com.example.ws.microservices.firstmicroservices.entity.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.Employee;
import com.example.ws.microservices.firstmicroservices.entity.ShiftTimeWork;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "attendance", schema = "attendance_gd")
public class Attendance {

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('attendance_gd.attendance_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private ShiftTimeWork shift ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private AttendanceStatus status;

    @ColumnDefault("0")
    @Column(name = "hours_worked")
    private Double hoursWorked;

    @Column(name = "comment", length = Integer.MAX_VALUE)
    private String comment;

    @Size(max = 256)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 256)
    private String userId;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

}