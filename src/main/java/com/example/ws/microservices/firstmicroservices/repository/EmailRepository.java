package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.dto.EmailDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.EmailSupervisor;
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
