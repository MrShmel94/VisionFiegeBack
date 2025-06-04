package com.example.ws.microservices.firstmicroservices.repository.etc;

import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentsDepartmentRepository extends JpaRepository<DocumentsDepartment, Long> {

    @Query("""
               SELECT dd.idDocument.id, dd.idDepartment.name
               FROM DocumentsDepartment dd
            """)
    List<Object[]> getMapDepartment();

    @Modifying
    @Query("""
    DELETE FROM DocumentsDepartment dd
    WHERE dd.idDocument.id = :documentId
      AND dd.idDepartment.id IN :idsToRemove
    """)
    void deleteByDocumentIdAndDepartmentIds(@Param("documentId") Long documentId, @Param("idsToRemove") List<Long> idsToRemove);
}
