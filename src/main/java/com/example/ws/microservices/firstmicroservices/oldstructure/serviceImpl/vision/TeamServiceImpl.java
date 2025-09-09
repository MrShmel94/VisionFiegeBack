package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision.TeamRepository;
import com.example.ws.microservices.firstmicroservices.common.cache.TeamServiceCache;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.TeamService;
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
