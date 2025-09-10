package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.contacttype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactTypeRepositoryCache extends JpaRepository<ContactType, Integer> {

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.dto.templateTables.PhoneEmailTypeSupervisorDTO(
                p.id, p.name
            )
            FROM PhoneEmailTypeSupervisor p
            """)
    List<ContactTypeDTO> getAllType();
}
