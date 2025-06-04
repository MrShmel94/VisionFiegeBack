package com.example.ws.microservices.firstmicroservices.entity.attendance.gd;

import com.example.ws.microservices.firstmicroservices.entity.attendance.DaySchedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "schedule_template", schema = "attendance_gd")
public class ScheduleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "schedule", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, DaySchedule> schedule;

    @Size(max = 512)
    @NotNull
    @Column(name = "name_schedule_template", nullable = false, length = 512)
    private String nameScheduleTemplate;

    @Size(max = 256)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 256)
    private String userId;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

}