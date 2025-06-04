package com.example.ws.microservices.firstmicroservices.response;

import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.dto.UserRoleDTO;
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
