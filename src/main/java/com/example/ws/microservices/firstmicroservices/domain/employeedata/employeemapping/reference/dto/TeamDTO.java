package com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto;

import com.example.ws.microservices.firstmicroservices.dto.SiteAware;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TeamDTO implements SiteAware {

    private Integer id;
    private String name;
    private String siteName;

}
