package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceId implements Serializable {
    private Long id;
    private LocalDate date;
}