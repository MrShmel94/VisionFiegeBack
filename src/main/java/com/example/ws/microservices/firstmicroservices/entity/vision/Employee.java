package com.example.ws.microservices.firstmicroservices.entity.vision;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;


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

    @Column(name = "br_code", nullable = false, length = 128)
    private String brCode;

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @ColumnDefault("true")
    @Column(name = "is_work", nullable = false)
    private Boolean isWork = false;

    @ColumnDefault("false")
    @Column(name = "is_supervisor", nullable = false)
    private Boolean isSupervisor = false;

    @Column(name = "is_can_has_account", nullable = false)
    private Boolean isCanHasAccount;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validToAccount;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFromAccount;

    @Column(name = "sex", nullable = false, length = 4)
    private String sex;

    @Column(name = "site_id", nullable = false)
    private Integer siteId;

    @Column(name = "shift_id", nullable = false)
    private Integer shiftId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "country_id", nullable = false)
    private Integer countryId;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Column(name = "position_id", nullable = false)
    private Integer positionId;

    @Column(name = "agency_id", nullable = false)
    private Integer agencyId;

    @Column(name = "user_id", nullable = false)
    private String userId;
}