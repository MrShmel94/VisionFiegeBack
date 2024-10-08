package com.example.ws.microservices.firstmicroservices.entity.template;

import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "agency")
public class Agency {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @OneToMany(mappedBy = "agency", fetch = FetchType.LAZY)
    private List<EmployeeMapping> allEmployee;
}