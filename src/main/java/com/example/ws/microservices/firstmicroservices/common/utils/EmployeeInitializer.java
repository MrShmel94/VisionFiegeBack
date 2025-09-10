package com.example.ws.microservices.firstmicroservices.common.utils;

import com.example.ws.microservices.firstmicroservices.oldstructure.request.CreateEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.CreateEmployeeRequestList;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.service.EmployeeMappingService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public class EmployeeInitializer {

    public static void runOnce(EmployeeMappingService employeeMappingService, Validator validator) {
        try (InputStream is = new FileInputStream("/Users/vision/Downloads/PrepareDBForAzure.xlsx")) {
            List<CreateEmployeeRequest> employeeRequests = employeeMappingService.parse(is);

            CreateEmployeeRequestList requestList = new CreateEmployeeRequestList();
            requestList.setEmployees(employeeRequests);

            Set<ConstraintViolation<CreateEmployeeRequestList>> violations = validator.validate(requestList);
            if (!violations.isEmpty()) {
                System.err.println("Validation errors:");
                for (ConstraintViolation<CreateEmployeeRequestList> violation : violations) {
                    System.err.println(violation.getPropertyPath() + ": " + violation.getMessage());
                }
                return;
            }

            employeeMappingService.createEmployees(employeeRequests);
            System.out.println("Employee initialization completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
