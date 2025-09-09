package com.example.ws.microservices.firstmicroservices.oldstructure.response;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.role.RoleDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ResponseUsersNotVerification {

    private List<PreviewEmployeeDTO> users;
    private List<RoleDTO> roles;
}
