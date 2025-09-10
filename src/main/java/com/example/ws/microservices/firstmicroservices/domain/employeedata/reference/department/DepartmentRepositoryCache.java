package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepositoryCache extends JpaRepository<Department, Short> {

    @Query("SELECT a FROM Department a LEFT JOIN FETCH a.site")
    List<Department> findAllWithSite();

}
