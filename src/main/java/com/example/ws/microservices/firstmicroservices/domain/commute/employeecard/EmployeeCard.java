package com.example.ws.microservices.firstmicroservices.domain.commute.employeecard;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_card", schema = "commute")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cardNumber;
}
