package com.example.ws.microservices.firstmicroservices.repository.templateTables;

import com.example.ws.microservices.firstmicroservices.entity.template.Department;
import com.example.ws.microservices.firstmicroservices.entity.template.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Short> {

    @Query("SELECT a FROM Position a LEFT JOIN FETCH a.site")
    List<Position> findAllWithSite();

}
