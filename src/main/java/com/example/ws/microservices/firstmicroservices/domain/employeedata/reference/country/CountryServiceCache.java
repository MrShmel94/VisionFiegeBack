package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisPreLoader;

import java.util.List;

public interface CountryServiceCache extends RedisPreLoader<CountryDTO> {
    List<Country> findAll();
    List<CountryDTO> getAllFromDB();
}
