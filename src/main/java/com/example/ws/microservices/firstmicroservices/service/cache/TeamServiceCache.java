package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.TeamDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Team;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface TeamServiceCache extends CachingService<TeamDTO> {
    List<Team> findAll();
    List<TeamDTO> getAllFromDB();
}
