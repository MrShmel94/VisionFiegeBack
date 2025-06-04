package com.example.ws.microservices.firstmicroservices.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.repository.vision.PhoneEmailTypeSupervisorRepository;
import com.example.ws.microservices.firstmicroservices.repository.vision.TeamRepository;
import com.example.ws.microservices.firstmicroservices.service.cache.PhoneEmailTypeSupervisorServiceCache;
import com.example.ws.microservices.firstmicroservices.service.cache.TeamServiceCache;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.service.vision.PhoneEmailTypeSupervisorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneEmailTypeSupervisorServiceImpl implements PhoneEmailTypeSupervisorService {

    private final PhoneEmailTypeSupervisorRepository repository;
    private final PhoneEmailTypeSupervisorServiceCache phoneEmailTypeSupervisorServiceCache;
    private final RedisCacheService redisCacheService;

}
