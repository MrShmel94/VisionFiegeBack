package com.example.ws.microservices.firstmicroservices.request;

import com.example.ws.microservices.firstmicroservices.entity.vision.AiEmployee;
import com.example.ws.microservices.firstmicroservices.entity.vision.EmployeeMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeData {
    private EmployeeMapping employee;
    private AiEmployee additional;
}