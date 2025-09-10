package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PositionRepositoryCache extends JpaRepository<Position, Short> {

    @Query("SELECT a FROM Position a LEFT JOIN FETCH a.site")
    List<Position> findAllWithSite();

}
