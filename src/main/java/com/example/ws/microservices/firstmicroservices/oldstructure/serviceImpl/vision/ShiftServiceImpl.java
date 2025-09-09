package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision.ShiftRepository;
import com.example.ws.microservices.firstmicroservices.common.cache.ShiftServiceCache;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.ShiftService;
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
