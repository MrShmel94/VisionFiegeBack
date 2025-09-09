package com.example.ws.microservices.firstmicroservices.oldstructure.service.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.TypeDocumentsDTO;

import java.util.List;

public interface TypeDocumentsService {

    void saveTypeDocument(TypeDocumentsDTO typeDocumentsDTO);
    List<TypeDocumentsDTO> getAllTypeDocuments();

}
