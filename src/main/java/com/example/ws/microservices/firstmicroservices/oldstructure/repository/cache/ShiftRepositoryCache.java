package com.example.ws.microservices.firstmicroservices.oldstructure.repository.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShiftRepositoryCache extends JpaRepository<Shift, Short> {

    @Query("SELECT a FROM Shift a LEFT JOIN FETCH a.site")
    List<Shift> findAllWithSite();

}
