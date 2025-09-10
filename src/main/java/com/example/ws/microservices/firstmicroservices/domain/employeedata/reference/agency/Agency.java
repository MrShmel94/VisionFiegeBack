package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.agency;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.site.Site;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "agency")
public class Agency {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "user_id", nullable = false)
    private String userId;
}