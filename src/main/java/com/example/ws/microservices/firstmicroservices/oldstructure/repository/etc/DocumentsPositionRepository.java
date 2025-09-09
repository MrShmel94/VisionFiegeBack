package com.example.ws.microservices.firstmicroservices.oldstructure.repository.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.DocumentsPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentsPositionRepository extends JpaRepository<DocumentsPosition, Long> {

    @Query("""
               SELECT dp.idDocument.id, dp.idPosition.name
               FROM DocumentsPosition dp
            """)
    List<Object[]> getMapPositions();

    @Modifying
    @Query("""
    DELETE FROM DocumentsPosition dp
    WHERE dp.idDocument.id = :documentId
      AND dp.idPosition.id IN :idsToRemove
    """)
    void deleteByDocumentIdAndPositionIds(@Param("documentId") Long documentId, @Param("idsToRemove") List<Long> idsToRemove);
}
