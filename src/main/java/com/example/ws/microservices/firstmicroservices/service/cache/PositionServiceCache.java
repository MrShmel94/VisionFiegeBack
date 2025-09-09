package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.PositionDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.Position;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface PositionServiceCache extends CachingService<PositionDTO> {
    List<Position> getAllPositionDTO();
    List<PositionDTO> getAllFromDB();
}
