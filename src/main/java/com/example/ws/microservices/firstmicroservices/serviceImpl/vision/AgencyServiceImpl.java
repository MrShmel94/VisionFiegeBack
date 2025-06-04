package com.example.ws.microservices.firstmicroservices.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.repository.vision.AgencyRepository;
import com.example.ws.microservices.firstmicroservices.repository.vision.DepartmentRepository;
import com.example.ws.microservices.firstmicroservices.service.cache.AgencyServiceCache;
import com.example.ws.microservices.firstmicroservices.service.cache.DepartmentServiceCache;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.service.vision.AgencyService;
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
