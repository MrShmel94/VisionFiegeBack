package com.example.ws.microservices.firstmicroservices.service.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.PositionDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Position;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface PositionService extends CachingService<PositionDTO> {
    List<Position> findAll();
    List<PositionDTO> findAllWithSite();
}
