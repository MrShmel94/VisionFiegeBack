package com.example.ws.microservices.firstmicroservices.service.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.PositionDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.ShiftDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Position;
import com.example.ws.microservices.firstmicroservices.entity.template.Shift;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface ShiftService extends CachingService<ShiftDTO> {
    List<Shift> findAll();
    List<ShiftDTO> getAllFromDB();
}
