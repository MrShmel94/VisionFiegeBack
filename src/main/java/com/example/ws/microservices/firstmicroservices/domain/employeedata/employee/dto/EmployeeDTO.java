package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto;

import com.example.ws.microservices.firstmicroservices.common.security.annotations.MaskField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class EmployeeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -109585463877560596L;

    private Long id;
    private String expertis;
    @MaskField(minWeight = 30)
    private String brCode;
    private String firstName;
    private String lastName;
    private Boolean isWork;
    private String sex;
    private String siteName;
    private String shiftName;
    private String departmentName;
    private String teamName;
    private String positionName;
    private String agencyName;
    private Boolean isCanHasAccount;
    private Boolean isSupervisor;
    private LocalDate validToAccount;
    private LocalDate validFromAccount;

    private String temporaryAssignmentSiteName;
    private LocalDate temporaryAssignmentFrom;
    private LocalDate temporaryAssignmentTo;
}
