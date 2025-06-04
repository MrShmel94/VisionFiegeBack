package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.PositionDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Position;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface PositionServiceCache extends CachingService<PositionDTO> {
    List<Position> getAllPositionDTO();
    List<PositionDTO> getAllFromDB();
}
