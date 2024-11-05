package com.example.ws.microservices.firstmicroservices.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date expiration;

    @Column(nullable = false)
    private Boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt = new Date();

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
