package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.vision.AiEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiEmployeeRepository extends JpaRepository<AiEmployee, Integer> {
    Optional<AiEmployee> findByEmployeeId(Long employeeId);
}
