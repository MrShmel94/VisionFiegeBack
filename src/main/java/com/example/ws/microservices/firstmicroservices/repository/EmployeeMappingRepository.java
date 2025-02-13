package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeMappingRepository extends JpaRepository<EmployeeMapping, Long> {

    Optional<EmployeeMapping> findByExpertis(String expertis);

    @Query("SELECT e FROM EmployeeMapping e "
            + "JOIN FETCH e.site s "
            + "JOIN FETCH e.shift sh "
            + "JOIN FETCH e.department d "
            + "JOIN FETCH e.team t "
            + "JOIN FETCH e.position p "
            + "JOIN FETCH e.agency a "
            + "WHERE e.expertis IN :expertisList")
    List<EmployeeMapping> findByExpertisIn(@Param("expertisList") List<String> expertisList);

    @Query("SELECT e.expertis FROM EmployeeMapping e WHERE e.expertis IN :expertisSet OR e.zalosId IN :zalosIdSet OR e.brCode IN :brCodeSet")
    List<String> findExistingExpertis(@Param("expertisSet") Collection<String> expertisSet, @Param("zalosIdSet") Set<Short> zalosIdSet, @Param("brCodeSet") Collection<String> brCodeSet);
}
