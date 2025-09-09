package com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ClearPerformanceEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ClearPerformanceEmployeeRepository extends JpaRepository<ClearPerformanceEmployee, Long> {
    @Query("SELECT c FROM ClearPerformanceEmployee c WHERE c.date IN :dates")
    List<ClearPerformanceEmployee> findAllByDateIn(@Param("dates") Collection<LocalDate> dates);
}
