package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisPreLoader;

import java.util.List;

public interface ShiftServiceCache extends RedisPreLoader<ShiftDTO> {
    List<Shift> findAll();
    List<ShiftDTO> getAllFromDB();
}
