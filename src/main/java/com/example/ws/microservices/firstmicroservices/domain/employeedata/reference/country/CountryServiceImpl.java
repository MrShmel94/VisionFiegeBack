package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountryServiceCache countryServiceCache;
    private final RedisService redisService;

}
