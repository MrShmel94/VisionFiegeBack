package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.SiteAware;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CountryDTO implements SiteAware {
    private Integer id;
    private String name;
    private String siteName;

}
