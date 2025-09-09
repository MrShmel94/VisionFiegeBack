package com.example.ws.microservices.firstmicroservices.oldstructure.service.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.DocumentsPositionAssistance;

import java.util.List;
import java.util.Map;

public interface DocumentsPositionAssistanceService {

    Map<Long, List<String>> getMapDocumentsPositionAssistance();
    void savePositionMappingAssistance(Map<Long, List<String>> mapDocumentsPosition);
    void saveAll(List<DocumentsPositionAssistance> documentsPositions);
    void deleteByDocumentIdAndPositionAssistanceIds(Long documentId, List<Long> positionIds);
}
