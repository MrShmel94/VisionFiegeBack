package com.example.ws.microservices.firstmicroservices.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RoleDTO {

    private String name;
    private Integer roleId;
    private Integer weight;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

}
