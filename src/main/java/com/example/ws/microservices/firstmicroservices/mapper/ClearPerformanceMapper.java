package com.example.ws.microservices.firstmicroservices.mapper;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.ClearPerformanceGDDto;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.ClearPerformanceEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClearPerformanceMapper {

    ClearPerformanceMapper INSTANCE = Mappers.getMapper(ClearPerformanceMapper.class);

    ClearPerformanceGDDto toClearPerformanceGDD(ClearPerformanceEmployee clearPerformanceEmployee);
}
