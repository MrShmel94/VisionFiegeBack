package com.example.ws.microservices.firstmicroservices.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.repository.vision.CountryRepository;
import com.example.ws.microservices.firstmicroservices.repository.vision.ShiftRepository;
import com.example.ws.microservices.firstmicroservices.service.cache.CountryServiceCache;
import com.example.ws.microservices.firstmicroservices.service.cache.ShiftServiceCache;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.service.vision.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftServiceCache shiftServiceCache;
    private final RedisCacheService redisCacheService;

}
