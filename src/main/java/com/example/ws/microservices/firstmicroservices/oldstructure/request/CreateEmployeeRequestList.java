package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateEmployeeRequestList {
    @Valid
    private List<CreateEmployeeRequest> employees;
}
