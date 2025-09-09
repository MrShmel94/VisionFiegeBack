package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PreviewEmployeeDTO {

    private Long id;
    private String expertis;
    private String firstName;
    private String lastName;
    private String department;
    private String team;
    private String position;
    private String siteName;

    private List<UserRoleDTO> roles = new ArrayList<>();

    //FOR JPQL
    public PreviewEmployeeDTO(Long id, String expertis, String firstName, String lastName, String department, String team, String position, String siteName) {
        this.id = id;
        this.expertis = expertis;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.team = team;
        this.position = position;
        this.siteName = siteName;
    }

}
