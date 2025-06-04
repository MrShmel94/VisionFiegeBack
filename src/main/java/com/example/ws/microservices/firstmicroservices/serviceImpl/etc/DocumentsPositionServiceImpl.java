package com.example.ws.microservices.firstmicroservices.serviceImpl.etc;

import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsPosition;
import com.example.ws.microservices.firstmicroservices.repository.etc.DocumentsPositionRepository;
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
public class DocumentsPositionServiceImpl implements DocumentsPositionService {

    private final DocumentsPositionRepository documentsPositionRepository;
    private final RedisCacheService redisCacheService;

    @Override
    public Map<Long, List<String>> getMapDocumentsPosition() {
        Map<String, List<String>> cachedEntries = redisCacheService.getAllFromHash(
                "mappingPosition", new TypeReference<List<String>>() {}
        );

        if (!cachedEntries.isEmpty()) {
            return cachedEntries.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Long.valueOf(e.getKey()),
                            Map.Entry::getValue
                    ));
        }

        List<Object[]> dbResult = documentsPositionRepository.getMapPositions();

        Map<Long, List<String>> documentDepartments = dbResult.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));

        documentDepartments.replaceAll((key, value) -> value != null ? value : new ArrayList<>());

        documentDepartments.forEach((documentId, positions) ->
                redisCacheService.saveToHash("mappingPosition", documentId.toString(), positions)
        );

        return documentDepartments;
    }

    @Override
    public void savePositionMapping(Map<Long, List<String>> mapDocumentsPosition) {

    }

    @Override
    public void saveAll(List<DocumentsPosition> documentsPositions) {
        documentsPositionRepository.saveAll(documentsPositions);
    }

    @Override
    public void deleteByDocumentIdAndPositionIds(Long documentId, List<Long> positionIds) {
        documentsPositionRepository.deleteByDocumentIdAndPositionIds(documentId, positionIds);
    }
}
