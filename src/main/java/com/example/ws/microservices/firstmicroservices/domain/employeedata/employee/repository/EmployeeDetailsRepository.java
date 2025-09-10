package com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeDetailsRepository extends JpaRepository<EmployeeDetails, Integer> {
    Optional<EmployeeDetails> findByEmployeeId(Long employeeId);
}
