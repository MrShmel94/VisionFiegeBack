package com.example.ws.microservices.firstmicroservices.dto.templateTables;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CountryDTO implements SiteAware{
    private Short id;
    private String name;
    private String siteName;

}
