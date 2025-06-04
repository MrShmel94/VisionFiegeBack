package com.example.ws.microservices.firstmicroservices.repository.vision;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
