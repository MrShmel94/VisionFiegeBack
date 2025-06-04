package com.example.ws.microservices.firstmicroservices.repository.vision;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
