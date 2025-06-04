package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "check_name_file", schema = "performance_gd")
public class CheckNameFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Size(max = 256)
    @NotNull
    @Column(name = "id_user_loaded", nullable = false, length = 256)
    private String idUserLoaded;

    @Size(max = 256)
    @NotNull
    @Column(name = "name_file", nullable = false, length = 256)
    private String nameFile;

    @Size(max = 64)
    @NotNull
    @Column(name = "status", nullable = false, length = 64)
    private String status;

}