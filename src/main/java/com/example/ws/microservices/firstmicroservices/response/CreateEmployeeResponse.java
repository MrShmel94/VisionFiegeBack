package com.example.ws.microservices.firstmicroservices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeResponse {
    private int totalRequests;
    private int successful;
    private int failed;
    private List<String> errorMessages;
}
