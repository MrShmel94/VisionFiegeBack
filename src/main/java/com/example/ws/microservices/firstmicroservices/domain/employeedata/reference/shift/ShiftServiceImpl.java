package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftServiceCache shiftServiceCache;
    private final RedisService redisService;

}
