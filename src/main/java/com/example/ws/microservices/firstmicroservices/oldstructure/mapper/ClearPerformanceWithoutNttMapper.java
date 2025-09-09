package com.example.ws.microservices.firstmicroservices.oldstructure.mapper;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployeeWithoutNttTatig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClearPerformanceWithoutNttMapper {

    ClearPerformanceWithoutNttMapper INSTANCE = Mappers.getMapper(ClearPerformanceWithoutNttMapper.class);

    ClearPerformanceGDDto toClearPerformanceGDD(ClearPerformanceEmployeeWithoutNttTatig clearPerformanceEmployee);
}
