package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift;

import com.example.ws.microservices.firstmicroservices.common.cache.redice.CachingService;

import java.util.List;

public interface ShiftServiceCache extends CachingService<ShiftDTO> {
    List<Shift> findAll();
    List<ShiftDTO> getAllFromDB();
}
