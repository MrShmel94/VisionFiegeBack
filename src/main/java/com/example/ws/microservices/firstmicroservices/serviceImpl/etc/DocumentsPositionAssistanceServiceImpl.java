package com.example.ws.microservices.firstmicroservices.serviceImpl.etc;

import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsPosition;
import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsPositionAssistance;
import com.example.ws.microservices.firstmicroservices.repository.etc.DocumentsPositionAssistanceRepository;
import com.example.ws.microservices.firstmicroservices.repository.etc.DocumentsPositionRepository;
import com.example.ws.microservices.firstmicroservices.service.cache.PositionServiceCache;
import com.example.ws.microservices.firstmicroservices.service.etc.DocumentsPositionAssistanceService;
import com.example.ws.microservices.firstmicroservices.service.etc.DocumentsPositionService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentsPositionAssistanceServiceImpl implements DocumentsPositionAssistanceService {

    private final DocumentsPositionAssistanceRepository documentsPositionAssistanceRepository;
    private final RedisCacheService redisCacheService;

    @Override
    public Map<Long, List<String>> getMapDocumentsPositionAssistance() {
        Map<String, List<String>> cachedEntries = redisCacheService.getAllFromHash(
                "mappingPositionAssistance", new TypeReference<List<String>>() {}
        );

        if (!cachedEntries.isEmpty()) {
            return cachedEntries.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Long.valueOf(e.getKey()),
                            Map.Entry::getValue
                    ));
        }

        List<Object[]> dbResult = documentsPositionAssistanceRepository.getMapPositionsAssistance();

        Map<Long, List<String>> documentDepartments = dbResult.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));

        documentDepartments.replaceAll((key, value) -> value != null ? value : new ArrayList<>());

        documentDepartments.forEach((documentId, positions) ->
                redisCacheService.saveToHash("mappingPositionAssistance", documentId.toString(), positions)
        );

        return documentDepartments;
    }

    @Override
    public void savePositionMappingAssistance(Map<Long, List<String>> mapDocumentsPosition) {

    }

    @Override
    public void saveAll(List<DocumentsPositionAssistance> documentsPositions) {
        documentsPositionAssistanceRepository.saveAll(documentsPositions);
    }

    @Override
    public void deleteByDocumentIdAndPositionAssistanceIds(Long documentId, List<Long> positionIds) {
        documentsPositionAssistanceRepository.deleteByDocumentIdAndPositionAssistanceIds(documentId, positionIds);
    }
}
