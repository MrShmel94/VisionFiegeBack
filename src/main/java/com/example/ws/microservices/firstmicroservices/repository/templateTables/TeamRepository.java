package com.example.ws.microservices.firstmicroservices.repository.templateTables;

import com.example.ws.microservices.firstmicroservices.entity.template.Shift;
import com.example.ws.microservices.firstmicroservices.entity.template.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Short> {

    @Query("SELECT a FROM Team a LEFT JOIN FETCH a.site")
    List<Team> findAllWithSite();

}
