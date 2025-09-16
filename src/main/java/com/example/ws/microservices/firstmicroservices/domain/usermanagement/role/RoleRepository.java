package com.example.ws.microservices.firstmicroservices.domain.usermanagement.role;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

    @Query("""
        SELECT new com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO(
            ur.id.userId, r.name, r.id, r.weight, COALESCE(ur.validFrom, CURRENT_DATE), COALESCE(ur.validTo, CURRENT_DATE)
        )
        FROM Role r
        LEFT JOIN UserRole ur ON r.id = ur.id.roleId
        WHERE ur.id.userId = :userId
        """)
    List<UserRoleDTO> findAllRolesByUserId(@Param("userId") Long userId);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.domain.usermanagement.role.RoleDTO(
           r.id, r.name, r.weight, r.description
           ) FROM Role r
           """)
    List<RoleDTO> findAllRolesByDTO();

    @Query("""
    SELECT new com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO(
        ur.id.userId,
        r.name,
        r.id,
        r.weight,
        ur.validFrom,
        ur.validTo
    )
    FROM UserRole ur
    JOIN Role r ON r.id = ur.id.roleId
    WHERE ur.id.userId IN :userIds
""")
    List<UserRoleDTO> findUserRoleDtosByUserIds(@Param("userIds") List<Long> userIds);
}
