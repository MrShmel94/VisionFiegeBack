package com.example.ws.microservices.firstmicroservices.service.etc;

import com.example.ws.microservices.firstmicroservices.dto.etc.TypeDocumentsDTO;

import java.util.List;

public interface TypeDocumentsService {

    void saveTypeDocument(TypeDocumentsDTO typeDocumentsDTO);
    List<TypeDocumentsDTO> getAllTypeDocuments();

}
