package com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
