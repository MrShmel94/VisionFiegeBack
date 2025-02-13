package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Site;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface SiteService {

    List<EmployeeDTO> findEmployeesByDynamicFilters(SiteRequestModel siteRequestModel);
    Optional<Site> findSiteByName(String name);
}
