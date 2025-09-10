package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepositoryCache extends JpaRepository<Team, Short> {

    @Query("SELECT a FROM Team a LEFT JOIN FETCH a.site")
    List<Team> findAllWithSite();

}
