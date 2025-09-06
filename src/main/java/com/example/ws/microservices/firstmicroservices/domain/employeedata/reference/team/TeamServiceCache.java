package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisPreLoader;

import java.util.List;

public interface TeamServiceCache extends RedisPreLoader<TeamDTO> {
    List<Team> findAll();
    List<TeamDTO> getAllFromDB();
}
