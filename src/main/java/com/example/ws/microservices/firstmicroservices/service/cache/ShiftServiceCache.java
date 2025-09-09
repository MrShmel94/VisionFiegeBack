package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.ShiftDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.Shift;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;

public interface ShiftServiceCache extends CachingService<ShiftDTO> {
    List<Shift> findAll();
    List<ShiftDTO> getAllFromDB();
}
