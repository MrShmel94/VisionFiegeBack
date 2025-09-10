package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ai_employee")
public class EmployeeDetails {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note", length = 1028)
    private String note;

    @Column(name = "date_start_contract", nullable = false)
    private LocalDate dateStartContract;

    @Column(name = "date_finish_contract", nullable = false)
    private LocalDate dateFinishContract;

    @Column(name = "date_bhp_now", nullable = false)
    private LocalDate dateBhpNow;

    @Column(name = "date_bhp_future", nullable = false)
    private LocalDate dateBhpFuture;

    @Column(name = "date_adr_now", nullable = false)
    private LocalDate dateAdrNow;

    @Column(name = "date_adr_future", nullable = false)
    private LocalDate dateAdrFuture;

    @ColumnDefault("0")
    @Column(name = "fte", nullable = false)
    private Double fte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeMapping employee;

    @Column(name = "user_id", nullable = false)
    private String userId;
}