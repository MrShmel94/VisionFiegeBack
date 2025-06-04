package com.example.ws.microservices.firstmicroservices.entity.vision;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employee")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeMapping {

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

    @Column(name = "sex", nullable = false, length = 4)
    private String sex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @Column(name = "is_can_has_account", nullable = false)
    private Boolean isCanHasAccount = false;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validToAccount;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFromAccount;

    @Column(name = "user_id", nullable = false)
    private String userId;
}
