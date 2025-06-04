package com.example.ws.microservices.firstmicroservices.mapper;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.PerformanceRowDTO;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.PerformanceRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PerformanceMapper {

    PerformanceMapper INSTANCE = Mappers.getMapper(PerformanceMapper.class);

    @Mapping(source = "activityName.name", target = "activityName")
    @Mapping(source = "finalCluster.name", target = "finalCluster")
    @Mapping(source = "activityCluster.name", target = "activityCluster")
    PerformanceRowDTO toPerformanceRowDto(PerformanceRow performance);
}
