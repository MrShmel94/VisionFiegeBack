package com.example.ws.microservices.firstmicroservices.repository.cache;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgencyRepositoryCache extends JpaRepository<Agency, Short> {

    @Query("SELECT a FROM Agency a LEFT JOIN FETCH a.site")
    List<Agency> findAllWithSite();
}
