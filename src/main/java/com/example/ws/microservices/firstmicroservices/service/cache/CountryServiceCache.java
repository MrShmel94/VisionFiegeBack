package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.CountryDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.Country;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface CountryServiceCache extends CachingService<CountryDTO> {
    List<Country> findAll();
    List<CountryDTO> getAllFromDB();
}
