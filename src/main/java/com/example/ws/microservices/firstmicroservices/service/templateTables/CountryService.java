package com.example.ws.microservices.firstmicroservices.service.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.AgencyDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.CountryDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Agency;
import com.example.ws.microservices.firstmicroservices.entity.template.Country;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;
import java.util.Optional;

public interface CountryService extends CachingService<CountryDTO> {
    List<Country> findAll();
    List<CountryDTO> findAllWithSite();
}
