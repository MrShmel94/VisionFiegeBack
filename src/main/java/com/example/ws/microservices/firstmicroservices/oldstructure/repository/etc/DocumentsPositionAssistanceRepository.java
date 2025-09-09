package com.example.ws.microservices.firstmicroservices.oldstructure.repository.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.DocumentsPositionAssistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentsPositionAssistanceRepository extends JpaRepository<DocumentsPositionAssistance, Long> {

    @Query("""
               SELECT dp.idDocument.id, dp.idPosition.name
               FROM DocumentsPositionAssistance dp
            """)
    List<Object[]> getMapPositionsAssistance();

    @Modifying
    @Query("""
    DELETE FROM DocumentsPositionAssistance dp
    WHERE dp.idDocument.id = :documentId
      AND dp.idPosition.id IN :idsToRemove
    """)
    void deleteByDocumentIdAndPositionAssistanceIds(@Param("documentId") Long documentId, @Param("idsToRemove") List<Long> idsToRemove);
}
