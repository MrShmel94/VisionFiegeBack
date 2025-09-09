package com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
