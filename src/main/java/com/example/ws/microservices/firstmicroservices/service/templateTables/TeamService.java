package com.example.ws.microservices.firstmicroservices.service.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.ShiftDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.TeamDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Shift;
import com.example.ws.microservices.firstmicroservices.entity.template.Team;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface TeamService extends CachingService<TeamDTO> {
    List<Team> findAll();
    List<TeamDTO> getAllFromDB();
}
