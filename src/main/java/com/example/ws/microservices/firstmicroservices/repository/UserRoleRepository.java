package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.role.Role;
import com.example.ws.microservices.firstmicroservices.entity.role.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("SELECT r FROM Role r JOIN UserRole ur ON ur.role.id = r.id WHERE ur.user.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}
