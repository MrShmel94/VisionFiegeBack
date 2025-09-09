package com.example.ws.microservices.firstmicroservices.oldstructure.repository;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.phonesupervisor.PhoneDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.phonesupervisor.PhoneSupervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhoneRepository extends JpaRepository<PhoneSupervisor, Integer> {

    @Query("""
            SELECT new com.example.ws.microservices.firstmicroservices.dto.PhoneDTO(
            e.id, e.phoneNumber, t.name
            )
            FROM PhoneSupervisor e
            LEFT JOIN e.typeId t
            WHERE e.employee.id = :employeeId
            """)
    List<PhoneDTO> findAllByPhoneSupervisorId(@Param("employeeId") Integer emailSupervisorId);
}
