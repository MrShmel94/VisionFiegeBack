package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
