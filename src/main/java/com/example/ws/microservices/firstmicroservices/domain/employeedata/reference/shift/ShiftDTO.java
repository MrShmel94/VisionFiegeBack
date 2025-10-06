package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.SiteAware;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ShiftDTO implements SiteAware {

    private Integer id;
    private String name;
    private String siteName;

}
