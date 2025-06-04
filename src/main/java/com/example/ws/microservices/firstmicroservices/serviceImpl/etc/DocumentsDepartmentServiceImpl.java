package com.example.ws.microservices.firstmicroservices.serviceImpl.etc;

import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsDepartment;
import com.example.ws.microservices.firstmicroservices.repository.etc.DocumentsDepartmentRepository;
import com.example.ws.microservices.firstmicroservices.service.etc.DocumentsDepartmentService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentsDepartmentServiceImpl implements DocumentsDepartmentService {

    private final DocumentsDepartmentRepository documentsDepartmentRepository;
    private final RedisCacheService redisCacheService;

    @Override
    public Map<Long, List<String>> getMapDocumentsDepartment() {
        Map<String, List<String>> cachedEntries = redisCacheService.getAllFromHash(
                "mappingDepartment", new TypeReference<List<String>>() {}
        );

        if (!cachedEntries.isEmpty()) {
            return cachedEntries.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Long.valueOf(e.getKey()),
                            Map.Entry::getValue
                    ));
        }

        List<Object[]> dbResult = documentsDepartmentRepository.getMapDepartment();

        Map<Long, List<String>> documentDepartments = dbResult.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));

        documentDepartments.replaceAll((key, value) -> value != null ? value : new ArrayList<>());

        documentDepartments.forEach((documentId, positions) ->
                redisCacheService.saveToHash("mappingDepartment", documentId.toString(), positions)
        );

        return documentDepartments;
    }

    @Override
    public void saveAll(List<DocumentsDepartment> documentsDepartments) {
        documentsDepartmentRepository.saveAll(documentsDepartments);
    }

    @Override
    public void deleteByDocumentIdAndDepartmentIds(Long documentId, List<Long> idsToRemove) {
        documentsDepartmentRepository.deleteByDocumentIdAndDepartmentIds(documentId, idsToRemove);
    }
}
