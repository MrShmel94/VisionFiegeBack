package com.example.ws.microservices.firstmicroservices.domain.commute.route;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route", schema = "commute")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}
