package com.example.ws.microservices.firstmicroservices.repository.cache;

import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepositoryCache extends JpaRepository<Team, Short> {

    @Query("SELECT a FROM Team a LEFT JOIN FETCH a.site")
    List<Team> findAllWithSite();

}
