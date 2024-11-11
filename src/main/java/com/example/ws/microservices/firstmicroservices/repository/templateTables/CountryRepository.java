package com.example.ws.microservices.firstmicroservices.repository.templateTables;

import com.example.ws.microservices.firstmicroservices.entity.template.Agency;
import com.example.ws.microservices.firstmicroservices.entity.template.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Short> {

    @Query("SELECT a FROM Country a LEFT JOIN FETCH a.site")
    List<Country> findAllWithSite();

}
