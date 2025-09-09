package com.example.ws.microservices.firstmicroservices.oldstructure.service.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.DocumentDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.etc.DocumentEtcRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.etc.DocumentConfigurationSave;

import java.util.List;
import java.util.Optional;

public interface DocumentsService {

    void saveDocument(DocumentEtcRequest documentDTO);
    void updateDocument(Long documentId, DocumentEtcRequest documentEtcRequest);
    void deleteDocument(Long documentId);

    Optional<DocumentDTO> getDocumentByName(String documentName);
    List<DocumentDTO> getAllDocuments();
    List<DocumentDTO> getAllDocumentsByDepartment(String departmentName);
    List<DocumentDTO> getAllDocumentsByPosition(String positionName);

    List<DocumentDTO> getAllDocumentsFilterByDepartmentAndPosition(String departmentName, String positionName);

    DocumentConfigurationSave getConfigurationEtcSaveDocumentation();
}
