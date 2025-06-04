package com.example.ws.microservices.firstmicroservices.service.etc;

import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsPosition;
import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsPositionAssistance;

import java.util.List;
import java.util.Map;

public interface DocumentsPositionAssistanceService {

    Map<Long, List<String>> getMapDocumentsPositionAssistance();
    void savePositionMappingAssistance(Map<Long, List<String>> mapDocumentsPosition);
    void saveAll(List<DocumentsPositionAssistance> documentsPositions);
    void deleteByDocumentIdAndPositionAssistanceIds(Long documentId, List<Long> positionIds);
}
