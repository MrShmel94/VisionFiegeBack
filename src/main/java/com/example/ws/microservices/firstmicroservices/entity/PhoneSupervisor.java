package com.example.ws.microservices.firstmicroservices.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "phone_supervisor")
public class PhoneSupervisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "phone_number", nullable = false , length = 128)
    private String phoneNumber;

}