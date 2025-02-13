package com.example.ws.microservices.firstmicroservices.mapper;

import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.entity.AiEmployee;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.example.ws.microservices.firstmicroservices.request.CreateEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.request.UserDetailsRequestModel;
import com.example.ws.microservices.firstmicroservices.response.UserRest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "site", ignore = true)
    @Mapping(target = "shift", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "agency", ignore = true)
    EmployeeMapping toEmployeeMapping(CreateEmployeeRequest createEmployeeRequest);

    @Mapping(target = "employee", ignore = true)
    AiEmployee toAiEmployee(CreateEmployeeRequest createEmployeeRequest);
}
