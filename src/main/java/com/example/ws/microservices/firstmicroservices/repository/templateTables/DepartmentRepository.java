package com.example.ws.microservices.firstmicroservices.repository.templateTables;

import com.example.ws.microservices.firstmicroservices.entity.template.Country;
import com.example.ws.microservices.firstmicroservices.entity.template.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Short> {

    @Query("SELECT a FROM Department a LEFT JOIN FETCH a.site")
    List<Department> findAllWithSite();

}
