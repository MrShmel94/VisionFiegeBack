package com.example.ws.microservices.firstmicroservices.oldstructure.mapper;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClearPerformanceMapper {

    ClearPerformanceMapper INSTANCE = Mappers.getMapper(ClearPerformanceMapper.class);

    ClearPerformanceGDDto toClearPerformanceGDD(ClearPerformanceEmployee clearPerformanceEmployee);
}
