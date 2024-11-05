package com.example.ws.microservices.firstmicroservices.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "user_registration")
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5394912682131877193L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "expertis", nullable = false)
    private String expertis;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, length = 256)
    private String email;

    @Column(name = "encrypted_password", nullable = false, length = 256)
    private String encryptedPassword;

    @Column(name = "email_verification_token", length = 256)
    private String emailVerificationToken;

    @ColumnDefault("false")
    @Column(name = "email_verification_status", nullable = false)
    private Boolean emailVerificationStatus = false;


    @ColumnDefault("false")
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
}
