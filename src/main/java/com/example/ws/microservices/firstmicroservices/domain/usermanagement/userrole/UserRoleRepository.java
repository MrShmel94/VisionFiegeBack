package com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT r FROM Role r JOIN UserRole ur ON ur.id.roleId = r.id WHERE ur.id.userId = :userId")
    List<UserRole> findRolesByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT ur
        FROM UserRole ur
        WHERE ur.validTo <= :expirationDate
    """)
    List<UserRole> findExpiredRoles(@Param("expirationDate") LocalDateTime expirationDate);

    void deleteById(UserRoleId id);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.id.userId = :userId AND ur.id.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Integer roleId);
}
