package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.entity.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

    @Query("""
        SELECT new com.example.ws.microservices.firstmicroservices.dto.RoleDTO(
            r.name, r.id, r.weight, COALESCE(ur.validFrom, CURRENT_TIMESTAMP), COALESCE(ur.validTo, CURRENT_TIMESTAMP)
        )
        FROM Role r
        LEFT JOIN UserRole ur ON r.id = ur.id.roleId
        WHERE ur.id.userId = :userId
        """)
    List<RoleDTO> findAllRolesByUserId(@Param("userId") Long userId);
}
