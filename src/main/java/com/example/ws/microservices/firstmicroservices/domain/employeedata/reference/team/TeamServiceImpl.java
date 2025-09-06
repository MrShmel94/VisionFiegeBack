package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamServiceCache teamServiceCache;
    private final RedisService redisService;

}
