package com.example.ws.microservices.firstmicroservices.entity.role;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleId implements Serializable {

    private Long userId;
    private Long roleId;

}
