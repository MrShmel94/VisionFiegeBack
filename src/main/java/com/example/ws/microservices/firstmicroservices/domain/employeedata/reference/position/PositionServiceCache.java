package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisPreLoader;

import java.util.List;

public interface PositionServiceCache extends RedisPreLoader<PositionDTO> {
    List<Position> getAllPositionDTO();
    List<PositionDTO> getAllFromDB();
}
