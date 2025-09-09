package com.example.ws.microservices.firstmicroservices.entity.etc.planed;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "planed_employee", schema = "etc")
public class PlanedEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "planed_training_id", nullable = false)
    private PlanedTraining planedTraining;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_present", nullable = false)
    private Boolean isPresent = false;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "date", nullable = false)
    private Instant date;

    @Size(max = 512)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 512)
    private String userId;

}