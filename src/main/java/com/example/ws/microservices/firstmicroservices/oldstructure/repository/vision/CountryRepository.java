package com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
