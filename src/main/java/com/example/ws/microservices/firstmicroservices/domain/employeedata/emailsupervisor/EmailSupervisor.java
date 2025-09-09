package com.example.ws.microservices.firstmicroservices.domain.employeedata.emailsupervisor;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.Employee;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.phoneemailtypesupervisor.PhoneEmailTypeSupervisor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "email_supervisor")
public class EmailSupervisor {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, length = 256)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private PhoneEmailTypeSupervisor typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "user_id", nullable = false)
    private String userId;

}