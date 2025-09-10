package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country;

import com.example.ws.microservices.firstmicroservices.common.cache.redice.CachingService;

import java.util.List;

public interface CountryServiceCache extends CachingService<CountryDTO> {
    List<Country> findAll();
    List<CountryDTO> getAllFromDB();
}
