package com.example.ws.microservices.firstmicroservices.repository.vision;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.phoneemailtypesupervisor.PhoneEmailTypeSupervisor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneEmailTypeSupervisorRepository extends JpaRepository<PhoneEmailTypeSupervisor, Long> {
}
