package com.example.ws.microservices.firstmicroservices.entity;

import com.example.ws.microservices.firstmicroservices.entity.role.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "phone_email_type_supervisor")
public class PhoneEmailTypeSupervisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @OneToMany(mappedBy = "typeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhoneSupervisor> phones;

    @OneToMany(mappedBy = "typeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailSupervisor> emails;

}
