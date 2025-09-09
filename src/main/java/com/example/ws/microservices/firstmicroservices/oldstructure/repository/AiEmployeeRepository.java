package com.example.ws.microservices.firstmicroservices.oldstructure.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.aiemployee.AiEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiEmployeeRepository extends JpaRepository<AiEmployee, Integer> {
    Optional<AiEmployee> findByEmployeeId(Long employeeId);
}
