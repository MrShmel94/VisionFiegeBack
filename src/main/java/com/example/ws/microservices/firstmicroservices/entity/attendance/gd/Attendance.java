package com.example.ws.microservices.firstmicroservices.entity.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.vision.Employee;
import com.example.ws.microservices.firstmicroservices.entity.vision.ShiftTimeWork;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Department;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Site;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendance", schema = "attendance")
public class Attendance {

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

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