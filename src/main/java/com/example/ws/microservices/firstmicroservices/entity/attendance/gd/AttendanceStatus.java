package com.example.ws.microservices.firstmicroservices.entity.attendance.gd;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "attendance_status", schema = "attendance_gd")
public class AttendanceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('attendance_gd.attendance_status_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 32)
    @NotNull
    @Column(name = "status_code", nullable = false, length = 32)
    private String statusCode;

    @Size(max = 64)
    @NotNull
    @Column(name = "status_name", nullable = false, length = 64)
    private String statusName;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

}