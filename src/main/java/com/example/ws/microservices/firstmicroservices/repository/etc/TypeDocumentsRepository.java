package com.example.ws.microservices.firstmicroservices.repository.etc;

import com.example.ws.microservices.firstmicroservices.dto.etc.TypeDocumentsDTO;
import com.example.ws.microservices.firstmicroservices.entity.etc.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeDocumentsRepository extends JpaRepository<TypeDocument, Integer> {
}
