package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision.AgencyRepository;
import com.example.ws.microservices.firstmicroservices.common.cache.AgencyServiceCache;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.AgencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final AgencyRepository agencyRepository;
    private final AgencyServiceCache agencyServiceCache;
    private final RedisCacheService redisCacheService;

}
