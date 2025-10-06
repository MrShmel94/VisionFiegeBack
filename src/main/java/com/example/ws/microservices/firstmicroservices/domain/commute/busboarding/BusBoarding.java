package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding;

import com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.EmployeeCard;
import com.example.ws.microservices.firstmicroservices.domain.commute.route.Route;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusBoarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime boardedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeCardId", nullable = false)
    private EmployeeCard employeeCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routeId", nullable = false)
    private Route route;
}
