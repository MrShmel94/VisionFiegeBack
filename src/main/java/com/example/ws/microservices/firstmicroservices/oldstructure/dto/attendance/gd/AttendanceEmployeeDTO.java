package com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class AttendanceEmployeeDTO {

    private Long id;
    private String expertis;
    private String firstName;
    private String lastName;
    private String teamName;
    private String shiftName;
    private String positionName;
    private String departmentName;
    private String siteName;
    private String supervisor;

}
