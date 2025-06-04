package com.example.ws.microservices.firstmicroservices.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.CheckNameFileDTO;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.CheckNameFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CheckNameFileRepository extends JpaRepository<CheckNameFile, Long> {

    @Query("""
                SELECT new com.example.ws.microservices.firstmicroservices.dto.performance.gd.CheckNameFileDTO(
                    cnf.id, cnf.nameFile, CONCAT(e.firstName, ' ', e.lastName), cnf.date, cnf.status
                )
                FROM CheckNameFile cnf
                JOIN UserEntity ue ON cnf.idUserLoaded = ue.userId
                JOIN Employee e ON e.expertis = ue.expertis
                WHERE cnf.date BETWEEN :startDate AND :endDate
            """)
    List<CheckNameFileDTO> findAllByDateBetween(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}
