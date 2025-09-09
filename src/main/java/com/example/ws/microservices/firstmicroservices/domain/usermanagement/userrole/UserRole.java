package com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole;


import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "user_role")
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom = LocalDate.now();

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo = LocalDate.of(9999, 12, 31);

    @Column(name = "user_id_accept", nullable = false)
    private String userId;
}
