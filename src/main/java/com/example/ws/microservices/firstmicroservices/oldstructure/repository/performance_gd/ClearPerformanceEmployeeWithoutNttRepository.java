package com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployeeWithoutNttTatig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClearPerformanceEmployeeWithoutNttRepository extends JpaRepository<ClearPerformanceEmployeeWithoutNttTatig, Long> {
    List<ClearPerformanceEmployeeWithoutNttTatig> findAllByDateBetween(LocalDate start, LocalDate end);
}
