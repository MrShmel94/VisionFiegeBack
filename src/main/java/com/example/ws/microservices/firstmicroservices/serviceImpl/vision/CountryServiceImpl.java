package com.example.ws.microservices.firstmicroservices.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.repository.vision.AgencyRepository;
import com.example.ws.microservices.firstmicroservices.repository.vision.CountryRepository;
import com.example.ws.microservices.firstmicroservices.service.cache.AgencyServiceCache;
import com.example.ws.microservices.firstmicroservices.service.cache.CountryServiceCache;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.service.vision.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountryServiceCache countryServiceCache;
    private final RedisCacheService redisCacheService;

}
