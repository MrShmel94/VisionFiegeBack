package com.example.ws.microservices.firstmicroservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_supervisors")
@IdClass(EmployeeSupervisorId.class)
public class EmployeeSupervisor {

    @Id
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Id
    @Column(name = "supervisor_expertis", nullable = false)
    private String supervisorExpertis;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
}
