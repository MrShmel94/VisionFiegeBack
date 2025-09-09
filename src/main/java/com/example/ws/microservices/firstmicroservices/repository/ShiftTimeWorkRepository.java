package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.domain.attendace.shifttimework.ShiftTimeWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShiftTimeWorkRepository extends JpaRepository<ShiftTimeWork, Short> {

    @Query(
            value = """
            SELECT stw.id, stw.name, stw.shift_code, stw.start_shift, stw.end_shift, stw.site_id
            FROM vision.shift_time_work stw
            JOIN vision.site s on stw.site_id = s.id
            WHERE s.name = :siteName
            """, nativeQuery = true
    )
    List<ShiftTimeWork> findShiftTimeWorkBySiteName(@Param("siteName") String siteName);
}
