package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.contacttype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhoneEmailTypeSupervisorRepositoryCache extends JpaRepository<PhoneEmailTypeSupervisor, Integer> {

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.dto.templateTables.PhoneEmailTypeSupervisorDTO(
                p.id, p.name
            )
            FROM PhoneEmailTypeSupervisor p
            """)
    List<PhoneEmailTypeSupervisorDTO> getAllType();
}
