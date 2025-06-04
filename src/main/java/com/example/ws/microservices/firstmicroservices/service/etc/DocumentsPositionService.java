package com.example.ws.microservices.firstmicroservices.service.etc;

import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsPosition;
import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsPositionAssistance;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface DocumentsPositionService {

    Map<Long, List<String>> getMapDocumentsPosition();
    void savePositionMapping(Map<Long, List<String>> mapDocumentsPosition);
    void saveAll(List<DocumentsPosition> documentsPositions);
    void deleteByDocumentIdAndPositionIds(Long documentId, List<Long> positionIds);
}
