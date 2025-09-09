package com.example.ws.microservices.firstmicroservices.oldstructure.repository.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeDocumentsRepository extends JpaRepository<TypeDocument, Integer> {
}
