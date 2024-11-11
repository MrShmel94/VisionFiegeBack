package com.example.ws.microservices.firstmicroservices.repository.templateTables;

import com.example.ws.microservices.firstmicroservices.entity.template.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgencyRepository extends JpaRepository<Agency, Short> {

    @Query("SELECT a FROM Agency a LEFT JOIN FETCH a.site")
    List<Agency> findAllWithSite();
}
