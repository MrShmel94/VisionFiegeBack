package com.example.ws.microservices.firstmicroservices.common.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.PositionDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Position;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.CachingService;

import java.util.List;

public interface PositionServiceCache extends CachingService<PositionDTO> {
    List<Position> getAllPositionDTO();
    List<PositionDTO> getAllFromDB();
}
