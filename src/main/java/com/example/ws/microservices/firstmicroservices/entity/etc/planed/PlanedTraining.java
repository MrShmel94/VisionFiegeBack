package com.example.ws.microservices.firstmicroservices.entity.etc.planed;

import com.example.ws.microservices.firstmicroservices.entity.etc.Document;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "planed_training", schema = "etc")
public class PlanedTraining {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "time_start", nullable = false)
    private LocalTime timeStart;

    @NotNull
    @Column(name = "time_finish", nullable = false)
    private LocalTime timeFinish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "positions", nullable = false)
    private List<String> positions;

    @NotNull
    @Column(name = "name_trainers", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> nameTrainers;

    @Size(max = 256)
    @NotNull
    @Column(name = "place", nullable = false, length = 256)
    private String place;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "is_auto_training")
    private Boolean isAutoTraining = false;

    @NotNull
    @Column(name = "max_count_employee")
    private Integer maxCountEmployee = 1;

    @NotNull
    @Column(name = "user_id")
    private String userId;
}