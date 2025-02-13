package com.example.ws.microservices.firstmicroservices.entity.role;


import com.example.ws.microservices.firstmicroservices.entity.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_role")
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("userId")
//    @JoinColumn(name = "user_id")
//    private Employee user;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("roleId")
//    @JoinColumn(name = "role_id")
//    private Role role;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom = LocalDateTime.now();

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
}
