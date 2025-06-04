package com.example.ws.microservices.firstmicroservices.repository.vision;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
}
