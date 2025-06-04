package com.example.ws.microservices.firstmicroservices.repository.cache;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepositoryCache extends JpaRepository<Country, Short> {

    @Query("SELECT a FROM Country a LEFT JOIN FETCH a.site")
    List<Country> findAllWithSite();

}
