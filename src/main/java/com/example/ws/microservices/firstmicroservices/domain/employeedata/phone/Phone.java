package com.example.ws.microservices.firstmicroservices.domain.employeedata.phone;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.Employee;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.contacttype.PhoneEmailTypeSupervisor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "phone_supervisor")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "phone_number", nullable = false , length = 128)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private PhoneEmailTypeSupervisor typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "user_id", nullable = false)
    private String userId;
}