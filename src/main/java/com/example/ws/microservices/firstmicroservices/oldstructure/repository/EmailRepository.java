package com.example.ws.microservices.firstmicroservices.oldstructure.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.emailsupervisor.EmailDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.emailsupervisor.EmailSupervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailRepository extends JpaRepository<EmailSupervisor, Integer> {

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.dto.EmailDTO(
            e.id, e.email, t.name
            )
            FROM EmailSupervisor e
            LEFT JOIN e.typeId t
            WHERE e.employee.id = :employeeId
            """)
    List<EmailDTO> findAllByEmailSupervisorId(@Param("employeeId") Integer emailSupervisorId);
}
