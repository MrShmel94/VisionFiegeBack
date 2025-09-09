package com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckNameFileDTO{
    private Long id;
    private String fileName;
    private String fullName;
    private LocalDate date;
    private String status;
}
