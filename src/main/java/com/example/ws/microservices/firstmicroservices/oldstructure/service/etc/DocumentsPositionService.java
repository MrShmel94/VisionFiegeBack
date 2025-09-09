package com.example.ws.microservices.firstmicroservices.oldstructure.service.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.DocumentsPosition;

import java.util.List;
import java.util.Map;

public interface DocumentsPositionService {

    Map<Long, List<String>> getMapDocumentsPosition();
    void savePositionMapping(Map<Long, List<String>> mapDocumentsPosition);
    void saveAll(List<DocumentsPosition> documentsPositions);
    void deleteByDocumentIdAndPositionIds(Long documentId, List<Long> positionIds);
}
