package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.CountryDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Country;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface CountryServiceCache extends CachingService<CountryDTO> {
    List<Country> findAll();
    List<CountryDTO> getAllFromDB();
}
