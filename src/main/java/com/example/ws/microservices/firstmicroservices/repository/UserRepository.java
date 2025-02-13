package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);

    @Query("""
        SELECT new com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO(
            ur.id, ur.expertis, e.zalosId, e.brCode, e.firstName, e.lastName, e.isWork, e.sex,
            s.name, sh.name, d.name, t.name, p.name, a.name, ur.userId, ur.email, ur.isVerified, ur.emailVerificationStatus,
            e.isCanHasAccount, e.validToAccount, ai.note, ai.dateStartContract, ai.dateFinishContract, ai.dateBhpNow, ai.dateBhpFuture,
            ai.dateAdrNow, ai.dateAdrFuture, ur.encryptedPassword
        )
        FROM UserEntity ur
        LEFT JOIN ur.employee e
        LEFT JOIN Site s ON e.siteId = s.id
        LEFT JOIN Shift sh ON e.shiftId = sh.id
        LEFT JOIN Department d ON e.departmentId = d.id
        LEFT JOIN Team t ON e.teamId = t.id
        LEFT JOIN Position p ON e.positionId = p.id
        LEFT JOIN Agency a ON e.agencyId = a.id
        LEFT JOIN AiEmployee ai ON ai.employee.id = e.id
        WHERE ur.expertis = :param OR ur.email = :param OR ur.userId = :param
    """)
    Optional<SupervisorAllInformationDTO> findByAnyUserAttribute(@Param("param") String identifier);

    Optional<UserEntity> findByEmailVerificationToken(String token);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email OR u.expertis = :expertis")
    Optional<UserEntity> findByEmailOrExpertis(@Param("email") String email, @Param("expertis") String expertis);

    @Modifying
    @Query("UPDATE UserEntity u SET u.isVerified = true WHERE u.userId = :userId")
    void setVerified(String userId);
}
