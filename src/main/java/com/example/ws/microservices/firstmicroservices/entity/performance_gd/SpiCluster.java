package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "spi_cluster", schema = "performance_gd")
public class SpiCluster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "name_table", nullable = false, length = 64)
    private String nameTable;
}