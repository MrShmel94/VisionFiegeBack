package com.example.ws.microservices.firstmicroservices.repository.templateTables;

import com.example.ws.microservices.firstmicroservices.entity.template.Position;
import com.example.ws.microservices.firstmicroservices.entity.template.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Short> {

    @Query("SELECT a FROM Shift a LEFT JOIN FETCH a.site")
    List<Shift> findAllWithSite();

}
