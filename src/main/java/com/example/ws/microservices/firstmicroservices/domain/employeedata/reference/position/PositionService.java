package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position;

import java.util.List;

public interface PositionService {

    List<PositionDTO> getPositions();
    List<PositionDTO> getPositionsBySupervisorSite(String userId);
    List<PositionDTO> getPositionsBySupervisorSite();
}
