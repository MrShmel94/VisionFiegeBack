package com.example.ws.microservices.firstmicroservices.repository.vision;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
