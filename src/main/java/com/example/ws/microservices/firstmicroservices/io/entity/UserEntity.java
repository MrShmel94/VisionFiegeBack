package com.example.ws.microservices.firstmicroservices.io.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;

@Entity(name="users")
@Data
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 5394912682131877193L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;
    private String emailVerificationToken;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean emailVerificationStatus;
}
