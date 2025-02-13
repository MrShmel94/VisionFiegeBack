package com.example.ws.microservices.firstmicroservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {

    private String name;
    private Integer roleId;
    private Integer weight;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

}
