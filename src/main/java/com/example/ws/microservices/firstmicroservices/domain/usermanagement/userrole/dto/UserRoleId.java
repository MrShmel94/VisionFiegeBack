package com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleId implements Serializable {
    private Long userId;
    private Integer roleId;
}
