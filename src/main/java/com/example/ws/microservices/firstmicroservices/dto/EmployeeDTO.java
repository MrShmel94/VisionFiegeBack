package com.example.ws.microservices.firstmicroservices.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class EmployeeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -109585463877560596L;

    private Long id;
    private String expertis;
    private Short zalosId;
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

}
