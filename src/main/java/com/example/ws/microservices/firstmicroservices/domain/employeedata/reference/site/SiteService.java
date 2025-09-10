package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.site;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.SiteRequestModel;

import java.util.List;
import java.util.Optional;

public interface SiteService {

    List<EmployeeDTO> findEmployeesByDynamicFilters(SiteRequestModel siteRequestModel);
    Optional<Site> findSiteByName(String name);
    List<SiteDTO> getSites();
}
