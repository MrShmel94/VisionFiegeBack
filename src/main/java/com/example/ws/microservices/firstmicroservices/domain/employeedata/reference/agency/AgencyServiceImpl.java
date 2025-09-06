package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.agency;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final AgencyRepository agencyRepository;
    private final AgencyRedisPreLoader agencyRedisPreLoader;
    private final RedisService redisService;

}
