package com.example.ws.microservices.firstmicroservices.oldstructure.repository.vision;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
