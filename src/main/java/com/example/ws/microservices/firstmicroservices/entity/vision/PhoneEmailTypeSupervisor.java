package com.example.ws.microservices.firstmicroservices.entity.vision;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @Column(name = "user_id", nullable = false)
    private String userId;

}
