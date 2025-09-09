package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision.PhoneEmailTypeSupervisorRepository;
import com.example.ws.microservices.firstmicroservices.common.cache.PhoneEmailTypeSupervisorServiceCache;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.PhoneEmailTypeSupervisorService;
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
