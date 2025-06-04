package com.example.ws.microservices.firstmicroservices.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.repository.vision.ShiftRepository;
import com.example.ws.microservices.firstmicroservices.repository.vision.TeamRepository;
import com.example.ws.microservices.firstmicroservices.service.cache.ShiftServiceCache;
import com.example.ws.microservices.firstmicroservices.service.cache.TeamServiceCache;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.service.vision.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamServiceCache teamServiceCache;
    private final RedisCacheService redisCacheService;

}
