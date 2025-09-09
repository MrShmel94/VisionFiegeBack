package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class InformationContractDTO {

    private String expertis;
    private LocalDate dateStartContract;
    private LocalDate dateEndContract;

}
