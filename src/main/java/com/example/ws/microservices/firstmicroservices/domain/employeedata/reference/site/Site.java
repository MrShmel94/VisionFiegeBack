package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.site;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team.Team;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.agency.Agency;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country.Country;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department.Department;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position.Position;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift.Shift;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "site")
public class Site {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "place", nullable = false, length = 64)
    private String place;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Shift> shifts;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> departments;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Position> positions;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Agency> agencies;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Country> counties;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Team> teams;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeMapping> employees;

    @Column(name = "user_id", nullable = false)
    private String userId;
}