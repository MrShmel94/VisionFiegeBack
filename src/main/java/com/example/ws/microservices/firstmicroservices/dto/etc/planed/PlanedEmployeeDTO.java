package com.example.ws.microservices.firstmicroservices.dto.etc.planed;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanedEmployeeDTO {
    private Long id;
    private Long idPlaningDocument;
    private String fullName;
    private String expertis;
    private Long idEmployee;
    private String sex;
    private String departmentName;
    private String teamName;
    private String shiftName;
    private String fullNameSupervisor;
    private String expertisSupervisor;
    @JsonInclude()
    private Boolean isPresent = false;
    private Instant date;
    private String userFullName;
}
