package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.contacttype;

import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
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
