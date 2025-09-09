package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.shifttimework.ShiftTimeWork;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "additional_effort", schema = "performance_gd")
public class AdditionalEffort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Size(max = 64)
    @NotNull
    @Column(name = "expertis", nullable = false, length = 64)
    private String expertis;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_name_id", nullable = false)
    private ActivityName activityName;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Double duration;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftTimeWork shift;

}