package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;

import java.util.List;

public interface SiteService {

    List<EmployeeDTO> findEmployeesBySiteName(String name);
    List<EmployeeDTO> findEmployeesByDepartment(SiteRequestModel siteRequestModel);

    List<EmployeeDTO> findEmployeesByDynamicFilters(SiteRequestModel siteRequestModel);


}
