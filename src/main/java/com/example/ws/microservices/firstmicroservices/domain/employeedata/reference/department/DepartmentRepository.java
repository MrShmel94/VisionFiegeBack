package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
