package com.example.ws.microservices.firstmicroservices.repository.vision;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
