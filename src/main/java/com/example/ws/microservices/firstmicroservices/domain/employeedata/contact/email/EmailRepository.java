package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Integer> {

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.email.EmailDTO(
            e.id, e.email, t.name
            )
            FROM Email e
            LEFT JOIN e.typeId t
            WHERE e.employee.id = :employeeId
            """)
    List<EmailDTO> findAllByEmailSupervisorId(@Param("employeeId") Integer emailSupervisorId);
}
