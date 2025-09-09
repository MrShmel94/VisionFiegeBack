package com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {

    private Long userId;
    private String name;
    private Integer roleId;
    private Integer weight;
    private LocalDate validFrom;
    private LocalDate validTo;

}
