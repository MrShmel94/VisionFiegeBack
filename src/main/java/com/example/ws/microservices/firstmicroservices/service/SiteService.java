package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.SiteDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Site;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;

import java.util.List;
import java.util.Optional;

public interface SiteService {

    List<EmployeeDTO> findEmployeesByDynamicFilters(SiteRequestModel siteRequestModel);
    Optional<Site> findSiteByName(String name);
    List<SiteDTO> getSites();
}
