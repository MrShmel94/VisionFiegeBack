package com.example.ws.microservices.firstmicroservices.oldstructure.mapper;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.Employee;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.CreateEmployeeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
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
    EmployeeDetails toAiEmployee(CreateEmployeeRequest createEmployeeRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    void updateAiEmployeeFromRequest(CreateEmployeeRequest request, @MappingTarget EmployeeDetails employeeDetails);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "siteId", ignore = true)
    @Mapping(target = "shiftId", ignore = true)
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "countryId", ignore = true)
    @Mapping(target = "teamId", ignore = true)
    @Mapping(target = "positionId", ignore = true)
    @Mapping(target = "agencyId", ignore = true)
    void updateEmployeeFromRequest(CreateEmployeeRequest request, @MappingTarget Employee employee);

    @Named("zeroToNull")
    public static Integer zeroToNull(Integer value) {
        return (value != null && value == 0) ? null : value;
    }
}
