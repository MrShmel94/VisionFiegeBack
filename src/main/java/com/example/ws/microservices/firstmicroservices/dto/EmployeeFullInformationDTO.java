package com.example.ws.microservices.firstmicroservices.dto;

import com.example.ws.microservices.firstmicroservices.secure.aspects.MaskField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFullInformationDTO {

    private Long id;
    private String expertis;
    private Short zalosId;
    @MaskField(minWeight = 30)
    private String brCode;
    private String firstName;
    private String lastName;
    private String sex;
    private String teamName;
    private String siteName;
    private String shiftName;
    private String countryName;
    private String departmentName;
    private String positionName;
    @JsonInclude()
    private Boolean isSupervisor;
    @JsonInclude()
    private Boolean isCanHasAccount;
    private LocalDate validToAccount;
    private LocalDate validFromAccount;
    private String agencyName;
    private String note;
    private LocalDate dateStartContract;
    private LocalDate dateFinishContract;
    private LocalDate dateBhpNow;
    private LocalDate dateBhpFuture;
    private LocalDate dateAdrNow;
    private LocalDate dateAdrFuture;
    private Double fte;

}
