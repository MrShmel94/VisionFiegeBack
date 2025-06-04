package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "check_header", schema = "performance_gd")
public class CheckHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "table_name", nullable = false, length = 128)
    private String tableName;

}