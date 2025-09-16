package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto.SupervisorFullNameDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto.SupervisorAllInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);

    @Query("""
        SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto.SupervisorAllInformationDTO(
            e.id, ur.expertis, e.brCode, e.firstName, e.lastName, e.isWork, e.sex,
            s.name, sh.name, d.name, t.name, p.name, a.name, ur.userId, ur.email, ur.isVerified, ur.emailVerificationStatus,
            e.isCanHasAccount, e.isSupervisor,  e.validToAccount, e.validFromAccount, ed.note, ed.dateStartContract, ed.dateFinishContract, ed.dateBhpNow, ed.dateBhpFuture,
            ed.dateAdrNow, ed.dateAdrFuture
        )
        FROM UserEntity ur
        LEFT JOIN ur.employee e
        LEFT JOIN Site s ON e.siteId = s.id
        LEFT JOIN Shift sh ON e.shiftId = sh.id
        LEFT JOIN Department d ON e.departmentId = d.id
        LEFT JOIN Team t ON e.teamId = t.id
        LEFT JOIN Position p ON e.positionId = p.id
        LEFT JOIN Agency a ON e.agencyId = a.id
        LEFT JOIN EmployeeDetails ed ON ed.employee.id = e.id
        WHERE ur.expertis = :param OR ur.email = :param OR ur.userId = :param
    """)
    Optional<SupervisorAllInformationDTO> findByAnyUserAttribute(@Param("param") String identifier);

    Optional<UserEntity> findByEmailVerificationToken(String token);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email OR u.expertis = :expertis")
    Optional<UserEntity> findByEmailOrExpertis(@Param("email") String email, @Param("expertis") String expertis);

    @Modifying
    @Query("UPDATE UserEntity u SET u.isVerified = true WHERE u.userId = :userId")
    void setVerified(String userId);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto.SupervisorFullNameDTO(
            e.firstName, e.lastName
            )
            FROM UserEntity ue
            JOIN ue.employee e
            WHERE ue.userId = :userId
           """)
    Optional<SupervisorFullNameDTO> findSmallInformationSupervisorDTOByUserId(@Param("userId") String userId);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO(
           e.id, e.expertis, e.firstName, e.lastName, d.name, t.name, p.name, s.name
           ) FROM Employee e
           JOIN UserEntity ue ON e.expertis = ue.expertis
           JOIN Department d ON e.departmentId = d.id
           JOIN Position p ON e.positionId = p.id
           JOIN Team t ON e.teamId = t.id
           JOIN Site s ON s.id = e.siteId
           WHERE ue.isVerified = false
           """)
    List<PreviewEmployeeDTO> getAllUsersWithoutVerification();

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO(
           e.id, e.expertis, e.firstName, e.lastName, d.name, t.name, p.name, s.name
           ) FROM Employee e
           JOIN UserEntity ue ON e.expertis = ue.expertis
           JOIN Department d ON e.departmentId = d.id
           JOIN Position p ON e.positionId = p.id
           JOIN Team t ON e.teamId = t.id
           JOIN Site s ON s.id = e.siteId
           WHERE ue.isVerified = true
           """)
    List<PreviewEmployeeDTO> getAllUsersAccount();

    @Query(value = """
           SELECT ue.encrypted_password
           FROM vision.user_registration AS ue
           WHERE ue.email = :userId
           """, nativeQuery = true)
    Optional<String> getUserEncryptedPassword (@Param("userId") String userId);
}
