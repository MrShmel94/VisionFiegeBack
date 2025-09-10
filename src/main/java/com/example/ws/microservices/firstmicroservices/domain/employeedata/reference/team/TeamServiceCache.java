package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team;

import com.example.ws.microservices.firstmicroservices.common.cache.redice.CachingService;

import java.util.List;

public interface TeamServiceCache extends CachingService<TeamDTO> {
    List<Team> findAll();
    List<TeamDTO> getAllFromDB();
}
