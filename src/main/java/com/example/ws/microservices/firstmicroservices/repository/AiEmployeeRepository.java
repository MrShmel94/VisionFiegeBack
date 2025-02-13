package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.AiEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiEmployeeRepository extends JpaRepository<AiEmployee, Integer> {
}
