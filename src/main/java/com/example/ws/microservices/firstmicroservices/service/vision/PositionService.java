package com.example.ws.microservices.firstmicroservices.service.vision;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.PositionDTO;

import java.util.List;

public interface PositionService {

    List<PositionDTO> getPositions();
    List<PositionDTO> getPositionsBySupervisorSite(String userId);
    List<PositionDTO> getPositionsBySupervisorSite();
}
