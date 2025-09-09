package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision.CountryRepository;
import com.example.ws.microservices.firstmicroservices.common.cache.CountryServiceCache;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.CountryService;
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
