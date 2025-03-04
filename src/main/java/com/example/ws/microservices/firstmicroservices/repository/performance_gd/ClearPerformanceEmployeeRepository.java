package com.example.ws.microservices.firstmicroservices.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.ClearPerformanceEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClearPerformanceEmployeeRepository extends JpaRepository<ClearPerformanceEmployee, Long> {
    List<ClearPerformanceEmployee> findAllByDateBetween(LocalDate start, LocalDate end);
}
