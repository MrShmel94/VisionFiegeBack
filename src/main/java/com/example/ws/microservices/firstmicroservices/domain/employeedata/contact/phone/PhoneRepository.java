package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.phone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Integer> {

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.phone.PhoneDTO(
            ph.id, ph.phoneNumber, t.name
            )
            FROM Phone ph
            LEFT JOIN ph.typeId t
            WHERE ph.employee.id = :employeeId
            """)
    List<PhoneDTO> findAllByPhoneSupervisorId(@Param("employeeId") Integer emailSupervisorId);
}
