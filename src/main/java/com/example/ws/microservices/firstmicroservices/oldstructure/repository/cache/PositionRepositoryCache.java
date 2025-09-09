package com.example.ws.microservices.firstmicroservices.oldstructure.repository.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PositionRepositoryCache extends JpaRepository<Position, Short> {

    @Query("SELECT a FROM Position a LEFT JOIN FETCH a.site")
    List<Position> findAllWithSite();

}
