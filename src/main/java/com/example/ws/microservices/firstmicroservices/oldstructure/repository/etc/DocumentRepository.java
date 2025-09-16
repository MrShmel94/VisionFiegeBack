package com.example.ws.microservices.firstmicroservices.oldstructure.repository.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.DocumentDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.DocumentDTO(
           d.id, d.name, d.description, td.name, d.url,
           d.version, d.dateLastUpd, d.dateStart, d.dateFinish, ui.expertis
           ) FROM Document d
           JOIN TypeDocument td ON d.typeDocument.id = td.id
           JOIN UserEntity ui ON ui.userId = d.userId
           """)
    List<DocumentDTO> findAllCustom();

    @Query("""
    SELECT new com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.DocumentDTO(
        d.id, d.name, d.description, td.name, d.url,
        d.version, d.dateLastUpd, d.dateStart, d.dateFinish, ui.expertis
    )
    FROM Document d
    JOIN d.typeDocument td
    JOIN UserEntity ui ON ui.userId = d.userId
    WHERE EXISTS (
        SELECT 1
        FROM DocumentsDepartment dd
        WHERE dd.idDocument.id = d.id
          AND dd.idDepartment.name = :departmentName
    )
    """)
    List<DocumentDTO> getAllDocumentsByDepartmentName(@Param("departmentName") String departmentName);

    Optional<Document> findByName(String name);
}
