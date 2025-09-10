package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeData {
    private EmployeeMapping employee;
    private EmployeeDetails additional;
}