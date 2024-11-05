package com.example.ws.microservices.firstmicroservices.entity;

import com.example.ws.microservices.firstmicroservices.entity.role.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "expertis", nullable = false, length = 128)
    private String expertis;

    @Column(name = "zalos_id", nullable = false)
    private Short zalosId;

    @Column(name = "br_code", nullable = false, length = 128)
    private String brCode;

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @ColumnDefault("true")
    @Column(name = "is_work", nullable = false)
    private Boolean isWork = false;

    @Column(name = "sex", nullable = false, length = 4)
    private String sex;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(name = "shift_id", nullable = false)
    private Long shiftId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "position_id", nullable = false)
    private Long positionId;

    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();
}